package com.zmbdp.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.zmbdp.api.RemoteJudgeService;
import com.zmbdp.api.domain.dto.JudgeSubmitDTO;
import com.zmbdp.api.domain.vo.UserQuestionResultVO;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.enums.ProgramType;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.utils.ThreadLocalUtil;
import com.zmbdp.common.security.exception.ServiceException;
import com.zmbdp.friend.domain.question.Question;
import com.zmbdp.friend.domain.question.QuestionCase;
import com.zmbdp.friend.domain.question.es.QuestionES;
import com.zmbdp.friend.domain.user.dto.UserSubmitDTO;
import com.zmbdp.friend.elasticsearch.QuestionRepository;
import com.zmbdp.friend.mapper.question.QuestionMapper;
import com.zmbdp.friend.mapper.user.UserSubmitMapper;
import com.zmbdp.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuestionServiceImpl implements IUserQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

//    @Autowired
//    private JudgeProducer judgeProducer;

    /**
     * 执行代码逻辑 service 层
     *
     * @param submitDTO 提交代码的一系列参数
     * @return 执行是否成功
     */
    @Override
    public Result<UserQuestionResultVO> submit(UserSubmitDTO submitDTO) {
        Integer programType = submitDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)) {
            // 按照 java 逻辑处理
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(submitDTO);
            // 拿到了执行所需要的参数之后再执行 service
            return remoteJudgeService.doJudgeJavaCode(judgeSubmitDTO);
        }
        throw new ServiceException(ResultCode.FAILED_NOT_SUPPORT_PROGRAM);
    }

    /**
     * 判题逻辑
     *
     * @param submitDTO 用户传过来的数据
     * @return 整理好的代码加上判题逻辑需要的参数
     */
    private JudgeSubmitDTO assembleJudgeSubmitDTO(UserSubmitDTO submitDTO) {
        Long questionId = submitDTO.getQuestionId();
        // 先查询 es 当中的数据
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        JudgeSubmitDTO judgeSubmitDTO = new JudgeSubmitDTO();
        if (questionES != null) {
            // 如果不是空就赋值
            BeanUtil.copyProperties(questionES, judgeSubmitDTO);
        } else {
            // 如果是空就从数据库种查，查到了再把数据库中的赋值
            Question question = questionMapper.selectById(questionId);
            BeanUtil.copyProperties(question, judgeSubmitDTO);
            questionES = new QuestionES();
            // 然后更新 es
            BeanUtil.copyProperties(question, questionES);
            questionRepository.save(questionES);
        }
        judgeSubmitDTO.setUserId(ThreadLocalUtil.get(Constants.USER_ID, Long.class));
        judgeSubmitDTO.setExamId(submitDTO.getExamId());
        judgeSubmitDTO.setProgramType(submitDTO.getProgramType());
        judgeSubmitDTO.setUserCode(codeConnect(submitDTO.getUserCode(), questionES.getMainFuc()));
        List<QuestionCase> questionCaseList = JSONUtil.toList(questionES.getQuestionCase(), QuestionCase.class);
        List<String> inputList = questionCaseList.stream().map(QuestionCase::getInput).toList();
        judgeSubmitDTO.setInputList(inputList);
        List<String> outputList = questionCaseList.stream().map(QuestionCase::getOutput).toList();
        judgeSubmitDTO.setOutputList(outputList);
        return judgeSubmitDTO;
    }

    /**
     * 把用户写的代码和 main 函数整理到一起
     *
     * @param userCode 用户写的代码
     * @param mainFunc main 函数
     * @return 整理好之后的代码
     */
    private String codeConnect(String userCode, String mainFunc) {
        String targetCharacter = "}";
        int targetLastIndex = userCode.lastIndexOf(targetCharacter);
        if (targetLastIndex != -1) {
            return userCode.substring(0, targetLastIndex) + "\n" + mainFunc + "\n" + userCode.substring(targetLastIndex);
        }
        throw new ServiceException(ResultCode.FAILED);
    }
}
