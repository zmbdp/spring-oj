package com.zmbdp.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.zmbdp.api.RemoteJudgeService;
import com.zmbdp.api.domain.UserExeResult;
import com.zmbdp.api.domain.dto.JudgeSubmitDTO;
import com.zmbdp.api.domain.vo.UserQuestionResultVO;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.enums.ProgramType;
import com.zmbdp.common.core.enums.QuestionResType;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.core.utils.ThreadLocalUtil;
import com.zmbdp.common.security.exception.ServiceException;
import com.zmbdp.friend.domain.exam.Exam;
import com.zmbdp.friend.domain.question.Question;
import com.zmbdp.friend.domain.question.QuestionCase;
import com.zmbdp.friend.domain.question.es.QuestionES;
import com.zmbdp.friend.domain.user.UserSubmit;
import com.zmbdp.friend.domain.user.dto.UserSubmitDTO;
import com.zmbdp.friend.elasticsearch.QuestionRepository;
import com.zmbdp.friend.mapper.exam.ExamMapper;
import com.zmbdp.friend.mapper.question.QuestionMapper;
import com.zmbdp.friend.mapper.user.UserSubmitMapper;
import com.zmbdp.friend.rabbit.JudgeProducer;
import com.zmbdp.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserQuestionServiceImpl extends BaseService implements IUserQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

    @Autowired
    private JudgeProducer judgeProducer;

    @Autowired
    private ExamMapper examMapper;

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
     * 提交代码逻辑（rabbitMQ 版本）
     *
     * @param submitDTO 提交代码的一系列参数
     * @return 执行是否成功
     */
    @Override
    public Result<Void> rabbitSubmit(UserSubmitDTO submitDTO) {
        Integer programType = submitDTO.getProgramType();
        // 如果 examId 不为空，就看看这个竞赛是否结束，如果是结束了就不能加分了
        if (submitDTO.getExamId() != null) {
            // 查询竞赛信息
            Exam exam = examMapper.selectById(submitDTO.getExamId());
            if (exam != null && exam.getEndTime() != null) {
                // 检查竞赛的结束时间是否早于当前时间，并且是没有分数的，就直接制空
                if (exam.getEndTime().isBefore(LocalDateTime.now())) {
                    // 如果竞赛已结束，将 submitDTO 的 examId 设置为 null
                    submitDTO.setExamId(null);
                }
            }
        }
        if (ProgramType.JAVA.getValue().equals(programType)) {
            // 按照 java 逻辑处理
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(submitDTO);
            // 处理完成之后传给 mq 去判题
            judgeProducer.produceMsg(judgeSubmitDTO);
            return toResult(true);
        }
        throw new ServiceException(ResultCode.FAILED_NOT_SUPPORT_PROGRAM);
    }

    /**
     * 获取代码执行结果的 service 层（rabbitMQ 版本）
     *
     * @param examId 竞赛 id
     * @param questionId 题目 id
     * @param currentTime 提交代码的时间
     * @return 运行结果
     */
    @Override
    public UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime) {
        // 获取用户 id
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        // 获取用户的提交代码，判题结果这些信息
        UserSubmit userSubmit = userSubmitMapper.selectCurrentUserSubmit(userId, examId, questionId, currentTime);
        UserQuestionResultVO resultVO = new UserQuestionResultVO();
        if (userSubmit == null) {
            // 如果获取不到就说明结果还没出来
            resultVO.setPass(QuestionResType.IN_JUDGE.getValue());
        } else {
            resultVO.setPass(userSubmit.getPass());
            resultVO.setExeMessage(userSubmit.getExeMessage());
            if (StrUtil.isNotEmpty(userSubmit.getCaseJudgeRes())) {
                // 如果提交结果不是空，就把 json 转换成对象返回给前端
                resultVO.setUserExeResultList(JSON.parseArray(userSubmit.getCaseJudgeRes(), UserExeResult.class));
            }
        }
        return resultVO;
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
