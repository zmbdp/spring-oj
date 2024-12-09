package com.zmbdp.friend.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.enums.ExamListType;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.common.security.exception.ServiceException;
import com.zmbdp.friend.domain.exam.Exam;
import com.zmbdp.friend.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.friend.domain.exam.dto.ExamRankDTO;
import com.zmbdp.friend.domain.exam.vo.ExamRankVO;
import com.zmbdp.friend.domain.exam.vo.ExamVO;
import com.zmbdp.friend.mapper.exam.ExamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExamCacheManager {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private RedisService redisService;

    public Long getListSize(Integer examListType) {
        String examListKey = getExamListKey(examListType);
        return redisService.getListSize(examListKey);
    }

    public Long getExamQuestionListSize(Long examId) {
        String examQuestionListKey = getExamQuestionListKey(examId);
        return redisService.getListSize(examQuestionListKey);
    }

    public Long getRankListSize(Long examId) {
        return redisService.getListSize(getExamRankListKey(examId));
    }

    public List<ExamVO> getExamVOList(ExamQueryDTO examQueryDTO) {
        int start = (examQueryDTO.getPageNum() - 1) * examQueryDTO.getPageSize();
        int end = start + examQueryDTO.getPageSize() - 1; // 下标需要 -1
        String examListKey = getExamListKey(examQueryDTO.getType());
        List<Long> examIdList = redisService.getCacheListByRange(examListKey, start, end, Long.class);
        List<ExamVO> examVOList = assembleExamVOList(examIdList);
        if (CollectionUtil.isEmpty(examVOList)) {
            // 说明 redis 中数据可能有问题 从数据库中查数据并且重新刷新缓存
            examVOList = getExamListByDB(examQueryDTO); // 从数据库中获取数据
            refreshCache(examQueryDTO.getType());
        }
        return examVOList;
    }

    public List<ExamRankVO> getExamRankList(ExamRankDTO examRankDTO) {
        int start = (examRankDTO.getPageNum() - 1) * examRankDTO.getPageSize();
        int end = start + examRankDTO.getPageSize() - 1; // 下标需要 -1
        return redisService.getCacheListByRange(getExamRankListKey(examRankDTO.getExamId()), start, end, ExamRankVO.class);
    }

    public void addUserExamCache(Long userId, Long examId) {
        String userExamListKey = getUserExamListKey(userId);
        redisService.leftPushForList(userExamListKey, examId);
    }

    public Long getFirstQuestion(Long examId) {
        return redisService.indexForList(getExamQuestionListKey(examId), 0, Long.class);
    }

    public Long preQuestion(Long examId, Long questionId) {
        Long index = redisService.indexOfForList(getExamQuestionListKey(examId), questionId);
        if (index == 0) {
            throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
        }
        return redisService.indexForList(getExamQuestionListKey(examId), index - 1, Long.class);
    }

    public Long nextQuestion(Long examId, Long questionId) {
        Long index = redisService.indexOfForList(getExamQuestionListKey(examId), questionId);
        long lastIndex = getExamQuestionListSize(examId) - 1;
        if (index == lastIndex) {
            throw new ServiceException(ResultCode.FAILED_LAST_QUESTION);
        }
        return redisService.indexForList(getExamQuestionListKey(examId), index + 1, Long.class);
    }

    //刷新缓存逻辑
    public void refreshCache(Integer examListType) {
        List<Exam> examList = new ArrayList<>();
        if (ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)) {
            // 查询未完赛的竞赛列表
            examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                    .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                    .gt(Exam::getEndTime, LocalDateTime.now())
                    .eq(Exam::getStatus, Constants.TRUE)
                    .orderByAsc(Exam::getCreateTime)
            );
        } else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
            // 查询历史竞赛
            examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                    .select(Exam::getExamId, Exam::getTitle, Exam::getStartTime, Exam::getEndTime)
                    .le(Exam::getEndTime, LocalDateTime.now())
                    .eq(Exam::getStatus, Constants.TRUE)
                    .orderByAsc(Exam::getCreateTime)
            );
        }
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
        redisService.deleteObject(getExamListKey(examListType));
        redisService.rightPushAll(getExamListKey(examListType), examIdList);
    }

    private List<ExamVO> getExamListByDB(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        //查询C端的竞赛列表
        return examMapper.selectExamList(examQueryDTO);
    }

    private List<ExamVO> assembleExamVOList(List<Long> examIdList) {
        if (CollectionUtil.isEmpty(examIdList)) {
            // 说明 redis 当中没数据 从数据库中查数据并且重新刷新缓存
            return null;
        }
        // 拼接 redis 当中 key 的方法 并且将拼接好的 key 存储到一个list中
        List<String> detailKeyList = new ArrayList<>();
        for (Long examId : examIdList) {
            detailKeyList.add(getDetailKey(examId));
        }
        List<ExamVO> examVOList = redisService.multiGet(detailKeyList, ExamVO.class);
        CollUtil.removeNull(examVOList);
        if (CollectionUtil.isEmpty(examVOList) || examVOList.size() != examIdList.size()) {
            // 说明 redis 中数据有问题 从数据库中查数据并且重新刷新缓存
            return null;
        }
        return examVOList;
    }

    private String getExamListKey(Integer examListType) {
        if (ExamListType.EXAM_UN_FINISH_LIST.getValue().equals(examListType)) {
            return CacheConstants.EXAM_UNFINISHED_LIST;
        } else if (ExamListType.EXAM_HISTORY_LIST.getValue().equals(examListType)) {
            return CacheConstants.EXAM_HISTORY_LIST;
        }
        return "";
    }

    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL + examId;
    }

    private String getUserExamListKey(Long userId) {
        return CacheConstants.USER_EXAM_LIST + userId;
    }

    private String getExamQuestionListKey(Long examId) {
        return CacheConstants.EXAM_QUESTION_LIST + examId;
    }

    private String getExamRankListKey(Long examId) {
        return CacheConstants.EXAM_RANK_LIST + examId;
    }
}