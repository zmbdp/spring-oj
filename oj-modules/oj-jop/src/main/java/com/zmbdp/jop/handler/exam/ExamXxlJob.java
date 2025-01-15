package com.zmbdp.jop.handler.exam;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.common.security.exception.ServiceException;
import com.zmbdp.jop.domain.exam.Exam;
import com.zmbdp.jop.domain.message.Message;
import com.zmbdp.jop.domain.message.MessageText;
import com.zmbdp.jop.domain.message.vo.MessageTextVO;
import com.zmbdp.jop.domain.user.UserScore;
import com.zmbdp.jop.mapper.exam.ExamMapper;
import com.zmbdp.jop.mapper.user.UserExamMapper;
import com.zmbdp.jop.mapper.user.UserSubmitMapper;
import com.zmbdp.jop.service.IMessageService;
import com.zmbdp.jop.service.IMessageTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExamXxlJob {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IMessageTextService messageTextService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private UserExamMapper userExamMapper;

    /**
     * 刷新 历史竞赛/当前竞赛 列表
     */
    @XxlJob("examListOrganizeHandler")
    public void examListOrganizeHandler() {
        log.info("*** examListOrganizeHandler 开始统计 ***");
        // 哪些竞赛应该放入历史竞赛中，哪些应该在未完赛的竞赛中，统计好之后放入缓存中
        // 直接执行两个的，第一个未过期
        List<Exam> unFinishList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .gt(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByAsc(Exam::getCreateTime)
        );
        refreshCache(unFinishList, CacheConstants.EXAM_UNFINISHED_LIST);
        // 第二个是过期了的比赛
        List<Exam> historyList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                .le(Exam::getEndTime, LocalDateTime.now())
                .eq(Exam::getStatus, Constants.TRUE)
                .orderByAsc(Exam::getCreateTime)
        );
        refreshCache(historyList, CacheConstants.EXAM_HISTORY_LIST);
        log.info("*** examListOrganizeHandler 统计结束 ***");
    }

    /**
     * 刷新用户消息
     */
    @XxlJob("examResultHandler")
    public void examResultHandler() {
        log.info("*** 刷新用户消息开始 ***");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusDateTime = now.minusDays(1); // 将当前时间减去 1 天，得到昨天的日期和时间。
        // 先统计竞赛
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .select(Exam::getExamId, Exam::getTitle)
                .eq(Exam::getStatus, Constants.TRUE)
                .ge(Exam::getEndTime, minusDateTime) // 结束时间大于等于昨天时间
                .le(Exam::getEndTime, now)); // 小于现在时间的
        if (CollectionUtil.isNotEmpty(examList)) {
            Set<Long> examIdSet = examList.stream().map(Exam::getExamId).collect(Collectors.toSet());
            // 根据分数排名，获得用户信息
            List<UserScore> userScoreList = userSubmitMapper.selectUserScoreList(examIdSet);
            Map<Long, List<UserScore>> userScoreMap = userScoreList.stream().collect(Collectors.groupingBy(UserScore::getExamId));
            // 然后再创建消息放到 redis 中
            createMessage(examList, userScoreMap);
        }
        log.info("*** 刷新用户消息结束 ***");
    }

    /**
     * 创建消息
     *
     * @param examList     消息列表
     * @param userScoreMap 用户表
     */
    private void createMessage(List<Exam> examList, Map<Long, List<UserScore>> userScoreMap) {
        List<MessageText> messageTextList = new ArrayList<>();
        List<Message> messageList = new ArrayList<>();
        for (Exam exam : examList) {
            Long examId = exam.getExamId();
            List<UserScore> userScoreList = userScoreMap.get(examId);
            int totalUser = userScoreList.size();
            int examRank = 1;
            for (UserScore userScore : userScoreList) {
                String msgTitle = exam.getTitle() + "——排名情况";
                String msgContent = "您所参与的竞赛：" + exam.getTitle()
                        + "，本次参与竞赛一共" + totalUser + "人， 您排名第" + examRank + "名！";
                userScore.setExamRank(examRank);
                MessageText messageText = new MessageText();
                messageText.setMessageTitle(msgTitle);
                messageText.setMessageContent(msgContent);
                messageText.setCreateBy(Constants.SYSTEM_USER_ID);
                messageTextList.add(messageText);
                Message message = new Message();
                message.setSendId(Constants.SYSTEM_USER_ID);
                message.setCreateBy(Constants.SYSTEM_USER_ID);
                message.setRecId(userScore.getUserId());
                messageList.add(message);
                examRank++;
            }
            try {
                userExamMapper.updateUserScoreAndRank(userScoreList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 先删除，再插入
            redisService.rightPushAll(getExamRankListKey(examId), userScoreList);
        }
        messageTextService.batchInsert(messageTextList);
        Map<String, MessageTextVO> messageTextVOMap = new HashMap<>();
        for (int i = 0; i < messageTextList.size(); i++) {
            MessageText messageText = messageTextList.get(i);
            MessageTextVO messageTextVO = new MessageTextVO();
            BeanUtil.copyProperties(messageText, messageTextVO);
            String msgDetailKey = getMsgDetailKey(messageText.getTextId());
            messageTextVOMap.put(msgDetailKey, messageTextVO);
            Message message = messageList.get(i);
            message.setTextId(messageText.getTextId());
        }
        messageService.batchInsert(messageList);
        // redis 操作
        Map<Long, List<Message>> userMsgMap = messageList.stream().collect(Collectors.groupingBy(Message::getRecId));
        Iterator<Map.Entry<Long, List<Message>>> iterator = userMsgMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<Message>> entry = iterator.next();
            Long recId = entry.getKey();
            String userMsgListKey = getUserMsgListKey(recId);
            List<Long> userMsgTextIdList = entry.getValue().stream().map(Message::getTextId).toList();
            redisService.rightPushAll(userMsgListKey, userMsgTextIdList);
        }
        redisService.multiSet(messageTextVOMap);
    }

    //刷新缓存逻辑
    public void refreshCache(List<Exam> examList, String examListKey) {
        if (CollectionUtil.isEmpty(examList)) {
            return;
        }
        Map<String, Exam> examMap = new HashMap<>();
        List<Long> examIdList = new ArrayList<>();
        for (Exam exam : examList) {
            examMap.put(getDetailKey(exam.getExamId()), exam);
            examIdList.add(exam.getExamId());
        }
        redisService.multiSet(examMap);
        redisService.deleteObject(examListKey);
        redisService.rightPushAll(examListKey, examIdList);
    }

    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL + examId;
    }

    private String getUserMsgListKey(Long userId) {
        return CacheConstants.USER_MESSAGE_LIST + userId;
    }

    private String getMsgDetailKey(Long textId) {
        return CacheConstants.MESSAGE_DETAIL + textId;
    }

    private String getExamRankListKey(Long examId) {
        return CacheConstants.EXAM_RANK_LIST + examId;
    }
}
