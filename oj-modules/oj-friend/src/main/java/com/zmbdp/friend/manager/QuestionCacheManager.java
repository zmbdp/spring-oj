package com.zmbdp.friend.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.common.security.exception.ServiceException;
import com.zmbdp.friend.domain.question.Question;
import com.zmbdp.friend.mapper.question.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionCacheManager {

    @Autowired
    private RedisService redisService;

    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 从缓存中拿到题目的个数
     *
     * @return 返回有几个数据
     */
    public Long getListSize() {
        return redisService.getListSize(CacheConstants.QUESTION_LIST);
    }

    /**
     * 从缓存中拿到
     *
     * @return
     */
    public Long getHostListSize() {
        return redisService.getListSize(CacheConstants.QUESTION_HOST_LIST);
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId).orderByDesc(Question::getCreateTime));
        if (CollectionUtil.isEmpty(questionList)) {
            return;
        }
        // 获取题目列表的 id
        List<Long> questionIdList = questionList.stream().map(Question::getQuestionId).toList();
        // 然后放到 redis 缓存中
        redisService.rightPushAll(CacheConstants.QUESTION_LIST, questionIdList);
    }

    /**
     * 从缓存中获取上一个题目的 id
     *
     * @param questionId 当前题目的 id
     * @return 上一个题目的 id
     */
    public Long preQuestion(Long questionId) {
        // 先拿到这一题的下标
        Long index = redisService.indexOfForList(CacheConstants.QUESTION_LIST, questionId);
        if (index == 0) {
            throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
        }
        // 然后返回上一个下标的题目 id
        return redisService.indexForList(CacheConstants.QUESTION_LIST, index - 1, Long.class);
    }

    /**
     * 从缓存中获取下一个题目的 id
     *
     * @param questionId 当前题目的 id
     * @return 下一个题目的 id
     */
    public Object nextQuestion(Long questionId) {
        Long index = redisService.indexOfForList(CacheConstants.QUESTION_LIST, questionId);
        long lastIndex = getListSize() - 1; // 计算最后一题的下标
        // 判断是不是最后一题
        if (index == lastIndex) {
            throw new ServiceException(ResultCode.FAILED_LAST_QUESTION);
        }
        return redisService.indexForList(CacheConstants.QUESTION_LIST, index + 1, Long.class);
    }

    public List<Long> getHostList() {
        return redisService.getCacheListByRange(CacheConstants.QUESTION_HOST_LIST,
                CacheConstants.DEFAULT_START, CacheConstants.DEFAULT_END, Long.class);
    }

    public void refreshHotQuestionList(List<Long> hotQuestionIdList) {
        if (CollectionUtil.isEmpty(hotQuestionIdList)) {
            return;
        }
        redisService.rightPushAll(CacheConstants.QUESTION_HOST_LIST, hotQuestionIdList);
    }
}
