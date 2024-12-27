package com.zmbdp.judge.controller;

import com.zmbdp.api.domain.dto.JudgeSubmitDTO;
import com.zmbdp.api.domain.vo.UserQuestionResultVO;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.judge.service.IJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/judge")
public class JudgeController {

    @Autowired
    private IJudgeService judgeService;

    @PostMapping("/doJudgeJavaCode")
    Result<UserQuestionResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO) {
        return Result.success(judgeService.doJudgeJavaCode(judgeSubmitDTO));
    }
}
