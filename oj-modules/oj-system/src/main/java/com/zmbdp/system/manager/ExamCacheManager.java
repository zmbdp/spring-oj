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

    /**
     * 添加竞赛的信息到 redis 中
     *
     * @param exam 竞赛信息
     */
    public void addCache(Exam exam) {
        redisService.leftPushForList(getExamListKey(), exam.getExamId());
        redisService.setCacheObject(getDetailKey(exam.getExamId()), exam);
    }

    /**
     * 删除 redis 中竞赛的数据
     *
     * @param examId 操作的竞赛id
     */
    public void deleteCache(Long examId) {
        redisService.removeForList(getExamListKey(), examId);
        redisService.deleteObject(getDetailKey(examId));
        redisService.deleteObject(getExamQuestionListKey(examId));
    }

    /**
     * 拿到 redis 中竞赛id列表的 key
     *
     * @return 竞赛id列表的 key
     */
    private String getExamListKey() {
        return CacheConstants.EXAM_UNFINISHED_LIST;
    }

    /**
     * 拿到更全面竞赛的数据
     *
     * @param examId 竞赛 id
     * @return 竞赛的全部信息
     */
    private String getDetailKey(Long examId) {
        return CacheConstants.EXAM_DETAIL + examId;
    }

    /**
     * 拿到竞赛题目关系表的 key
     *
     * @param examId 竞赛id
     * @return 竞赛题目关系数据
     */
    private String getExamQuestionListKey(Long examId) {
        return CacheConstants.EXAM_QUESTION_LIST + examId;
    }
}
