package com.zmbdp.system.manager;

import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.system.domain.exam.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamCacheManager {

    @Autowired
    private RedisService redisService;

    public void addCache(Exam exam) {
        redisService.leftPushForList(getExamListKey(), exam.getExamId());
        redisService.setCacheObject(getDetailKey(exam.getExamId()), exam);
    }

    public void deleteCache(Long examId) {
        redisService.removeForList(getExamListKey(), examId);
        redisService.deleteObject(getDetailKey(examId));
        redisService.deleteObject(getExamQuestionListKey(examId));
    }

    private String getExamListKey() {
        return CacheConstants.EXAM_UNFINISHED_LIST;
    }

    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL + examId;
    }

    private String getExamQuestionListKey(Long examId) {
        return CacheConstants.EXAM_QUESTION_LIST + examId;
    }
}
