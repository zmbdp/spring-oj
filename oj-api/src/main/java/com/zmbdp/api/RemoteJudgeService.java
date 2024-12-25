package com.zmbdp.api;

import com.zmbdp.api.domain.dto.JudgeSubmitDTO;
import com.zmbdp.api.domain.vo.UserQuestionResultVO;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(contextId = "RemoteJudgeService", value = Constants.JUDGE_SERVICE)
public interface RemoteJudgeService {

    @PostMapping("/judge/doJudgeJavaCode")
    Result<UserQuestionResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO);
}
