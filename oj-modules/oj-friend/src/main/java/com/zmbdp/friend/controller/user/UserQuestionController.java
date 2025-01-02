package com.zmbdp.friend.controller.user;

import com.zmbdp.api.domain.vo.UserQuestionResultVO;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.friend.aspect.CheckUserStatus;
import com.zmbdp.friend.domain.user.dto.UserSubmitDTO;
import com.zmbdp.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/question")
public class UserQuestionController {

    @Autowired
    private IUserQuestionService userQuestionService;

    /**
     * 提交代码接口
     *
     * @param submitDTO
     * @return
     */
    @PostMapping("/submit")
    public Result<UserQuestionResultVO> submit(@RequestBody UserSubmitDTO submitDTO) {
        return userQuestionService.submit(submitDTO);
    }

    /**
     * 提交代码结构（rabbitMQ 版本）
     *
     * @param submitDTO
     * @return
     */
    @CheckUserStatus
    @PostMapping("/rabbit/submit")
    public Result<Void> rabbitSubmit(@RequestBody UserSubmitDTO submitDTO) {
        return userQuestionService.rabbitSubmit(submitDTO);
    }

    /**
     * 获取代码执行结果的接口
     *
     * @param examId 竞赛 id
     * @param questionId 题目 id
     * @param currentTime 提交代码的时间
     * @return 执行结果
     */
    @GetMapping("/exe/result")
    public  Result<UserQuestionResultVO> exeResult(Long examId, Long questionId, String currentTime) {
        return Result.success(userQuestionService.exeResult(examId, questionId, currentTime));
    }
}
