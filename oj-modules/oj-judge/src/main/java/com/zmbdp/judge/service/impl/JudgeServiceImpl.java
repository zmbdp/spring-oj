package com.zmbdp.judge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zmbdp.api.domain.dto.JudgeSubmitDTO;
import com.zmbdp.api.domain.vo.UserQuestionResultVO;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.constants.JudgeConstants;
import com.zmbdp.common.core.enums.CodeRunStatus;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.judge.domain.SandBoxExecuteResult;
import com.zmbdp.judge.domain.UserSubmit;
import com.zmbdp.judge.mapper.UserSubmitMapper;
import com.zmbdp.judge.service.IJudgeService;
import com.zmbdp.judge.service.ISandboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JudgeServiceImpl extends BaseService implements IJudgeService {

    @Autowired
    private ISandboxService sandboxService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Override
    public UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO) {
        SandBoxExecuteResult sandBoxExecuteResult =
                sandboxService.exeJavaCode(judgeSubmitDTO.getUserCode(), judgeSubmitDTO.getInputList());
        UserQuestionResultVO userQuestionResultVO = new UserQuestionResultVO();
        if (sandBoxExecuteResult != null &&
                CodeRunStatus.SUCCEED.equals(sandBoxExecuteResult.getRunStatus())) {

        } else {
            userQuestionResultVO.setPass(Constants.FALSE);
            if (sandBoxExecuteResult != null) {
                userQuestionResultVO.setExeMessage(sandBoxExecuteResult.getExeMessage());
            } else {
                userQuestionResultVO.setExeMessage(CodeRunStatus.UNKNOWN_FAILED.getMsg());
            }
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
        }
        UserSubmit userSubmit = new UserSubmit();
        BeanUtil.copyProperties(userQuestionResultVO, userSubmit);
        userSubmit.setUserId(judgeSubmitDTO.getUserId());
        userSubmit.setQuestionId(judgeSubmitDTO.getQuestionId());
        userSubmit.setExamId(judgeSubmitDTO.getExamId());
        userSubmit.setProgramType(judgeSubmitDTO.getProgramType());
        userSubmit.setUserCode(judgeSubmitDTO.getUserCode());
        userSubmitMapper.insert(userSubmit);
        return userQuestionResultVO;
    }
}
