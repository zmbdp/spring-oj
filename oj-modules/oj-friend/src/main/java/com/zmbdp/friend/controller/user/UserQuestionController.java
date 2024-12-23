package com.zmbdp.friend.controller.user;

import com.zmbdp.api.domain.vo.UserQuestionResultVO;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.friend.domain.user.dto.UserSubmitDTO;
import com.zmbdp.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/question")
public class UserQuestionController {

    @Autowired
    private IUserQuestionService userQuestionService;

    @PostMapping("/submit")
    public Result<UserQuestionResultVO> submit(@RequestBody UserSubmitDTO submitDTO) {
        return userQuestionService.submit(submitDTO);
    }
}
