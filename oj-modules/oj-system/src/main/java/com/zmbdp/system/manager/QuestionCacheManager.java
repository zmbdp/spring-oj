package com.zmbdp.system.manager;

import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionCacheManager {

    @Autowired
    private RedisService redisService;

    /**
     * 添加题目刷新缓存
     *
     * @param questionId 题目id
     */
    public void addCache(Long questionId) {
        redisService.leftPushForList(CacheConstants.QUESTION_LIST, questionId);
    }

    /**
     * 删除题目刷新缓存
     *
     * @param questionId 题目id
     */
    public void deleteCache(Long questionId) {
        redisService.removeForList(CacheConstants.QUESTION_LIST, questionId);
    }
}