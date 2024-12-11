package com.zmbdp.friend.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmbdp.common.core.constants.HttpConstants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.security.service.TokenService;
import com.zmbdp.friend.domain.exam.Exam;
import com.zmbdp.friend.domain.exam.dto.ExamDTO;
import com.zmbdp.friend.domain.user.UserExam;
import com.zmbdp.friend.manager.ExamCacheManager;
import com.zmbdp.friend.mapper.exam.ExamMapper;
import com.zmbdp.friend.mapper.user.UserExamMapper;
import com.zmbdp.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserExamServiceImpl extends BaseService implements IUserExamService {
    @Autowired
    private UserExamMapper userExamMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 报名参赛 service 层
     * @param token 用户的身份信息
     * @param examId 竞赛 id
     * @return 是否成功报名
     */
    @Override
    public Result<Void> enter(String token, Long examId) {
        // 先通过数据库查询一下这个竞赛是否存在
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return Result.fail(ResultCode.FAILED_NOT_EXISTS);
        }
        // 再判断是否开赛
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            // 说明开赛，无法报名
            return Result.fail(ResultCode.EXAM_STARTED);
        }
        // 看看是否重复报名，从 token 获取 userId
        Long userId = tokenService.getUserId(token, secret);
        UserExam userExam = userExamMapper
                .selectOne(new LambdaQueryWrapper<UserExam>()
                        .eq(UserExam::getExamId, examId)
                        .eq(UserExam::getUserId, userId)
        );
        if (userExam != null) {
            // 如果不为空，说明已经报过名了，直接返回不能重复报名
            return Result.fail(ResultCode.USER_EXAM_HAS_ENTER);
        }
        // 说明没报过名，直接添加到数据库 和 redis 中
        // 添加到 redis 中
        examCacheManager.addUserExamCache(examId, userId);
        userExam = new UserExam();
        userExam.setExamId(examId);
        userExam.setUserId(userId);
        return toResult(userExamMapper.insert(userExam));
    }
}
