package com.zmbdp.friend.controller.user;

import com.zmbdp.common.core.constants.HttpConstants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.friend.domain.exam.dto.ExamDTO;
import com.zmbdp.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/exam")
public class UserExamController {

    @Autowired
    private IUserExamService userExamService;

    /**
     * 用户报名参加竞赛接口
     * @param token 用户信息
     * @param examDTO 竞赛的相关信息
     * @return 是否成功
     */
    @PostMapping("/enter")
    public Result<Void> enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody ExamDTO examDTO) {
        return userExamService.enter(token, examDTO.getExamId());
    }
}
