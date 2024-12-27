package com.zmbdp.judge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmbdp.api.domain.UserExeResult;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeServiceImpl extends BaseService implements IJudgeService {

    @Autowired
    private ISandboxService sandboxService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    /**
     * 用户答题 service 层
     *
     * @param judgeSubmitDTO 答题数据
     * @return 运行结果
     */
    @Override
    public UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO) {
        SandBoxExecuteResult sandBoxExecuteResult =
                sandboxService.exeJavaCode(judgeSubmitDTO.getUserCode(), judgeSubmitDTO.getInputList());
        UserQuestionResultVO userQuestionResultVO = new UserQuestionResultVO();
        if (sandBoxExecuteResult != null && CodeRunStatus.SUCCEED.equals(sandBoxExecuteResult.getRunStatus())) {
            // 有结果就比对参数，时间限制空间限制这些
            userQuestionResultVO = doJudge(judgeSubmitDTO, sandBoxExecuteResult, userQuestionResultVO);
        } else {
            // 没有结果或者运行错误说明直接错了
            userQuestionResultVO.setPass(Constants.FALSE);
            if (sandBoxExecuteResult != null) {
                userQuestionResultVO.setExeMessage(sandBoxExecuteResult.getExeMessage());
            } else {
                userQuestionResultVO.setExeMessage(CodeRunStatus.UNKNOWN_FAILED.getMsg());
            }
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
        }
        // 然后维护数据库中的数据
        saveUserSubmit(judgeSubmitDTO, userQuestionResultVO);
        return userQuestionResultVO;
    }

    /**
     * 结果比对的方法
     *
     * @param judgeSubmitDTO       题目参数
     * @param sandBoxExecuteResult 正常执行之后的结果
     * @param userQuestionResultVO 返回给前端的返回值
     */
    private static UserQuestionResultVO doJudge(JudgeSubmitDTO judgeSubmitDTO, SandBoxExecuteResult sandBoxExecuteResult, UserQuestionResultVO userQuestionResultVO) {
        // 比对结果、时间限制、空间限制
        List<String> outputList = judgeSubmitDTO.getOutputList();
        List<String> exeOutputList = sandBoxExecuteResult.getOutputList();
        // 如果两个输出的个数都不一样，那说明肯定错了
        if (exeOutputList.size() != outputList.size()) {
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return userQuestionResultVO;
        }
        // 说明此时数量是一样的，继续判断结果是否相同
        List<UserExeResult> userExeResultList = new ArrayList<>();
        boolean passed = resultCompare(judgeSubmitDTO, outputList, exeOutputList, userExeResultList);
        // 判断结果啊，时间限制空间限制这些
        return assembleUserQuestionResultVO(judgeSubmitDTO, sandBoxExecuteResult, userQuestionResultVO, userExeResultList, passed);
    }

    /**
     * 比对结果啊，时间限制空间限制这些，然后组装起来
     *
     * @param judgeSubmitDTO 用户输入数据
     * @param sandBoxExecuteResult 正常输出的结果
     * @param userQuestionResultVO 返回给用户的数据
     * @param userExeResultList 测试用例的输出
     * @param passed 结果是否正确
     * @return 返回给用户的数据
     */
    private static UserQuestionResultVO assembleUserQuestionResultVO(JudgeSubmitDTO judgeSubmitDTO, SandBoxExecuteResult sandBoxExecuteResult, UserQuestionResultVO userQuestionResultVO, List<UserExeResult> userExeResultList, boolean passed) {
        userQuestionResultVO.setUserExeResultList(userExeResultList);
        if (!passed) {
            // 说明是错的
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg());
            return userQuestionResultVO;
        }
        // 说明结果都对，继续比较时间限制和空间限制
        if (sandBoxExecuteResult.getUseMemory() > judgeSubmitDTO.getSpaceLimit()) {
            // 说明大于空间限制了
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.OUT_OF_MEMORY.getMsg());
            return userQuestionResultVO;
        }
        if (sandBoxExecuteResult.getUseTime() > judgeSubmitDTO.getTimeLimit()) {
            // 说明大于时间限制了
            userQuestionResultVO.setPass(Constants.FALSE);
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
            userQuestionResultVO.setExeMessage(CodeRunStatus.OUT_OF_TIME.getMsg());
            return userQuestionResultVO;
        }
        // 说明都是对的
        userQuestionResultVO.setPass(Constants.TRUE);
        // 得分等于题目难度乘上 100
        int score = judgeSubmitDTO.getDifficulty() * JudgeConstants.DEFAULT_SCORE;
        userQuestionResultVO.setScore(score);
        return userQuestionResultVO;
    }

    /**
     * 比对 output 的输出结果
     *
     * @param judgeSubmitDTO 前端写的代码数据
     * @param outputList 用户代码的输出列表
     * @param exeOutputList 正常的输出列表
     * @param userExeResultList 返回给前端的数据
     * @return 结果是否正确
     */
    private static boolean resultCompare(JudgeSubmitDTO judgeSubmitDTO, List<String> outputList, List<String> exeOutputList, List<UserExeResult> userExeResultList) {
        boolean passed = true;
        for (int index = 0; index < outputList.size(); index++) {
            String output = outputList.get(index);
            String exeOutput = exeOutputList.get(index);
            String input = judgeSubmitDTO.getInputList().get(index);
            UserExeResult userExeResult = new UserExeResult();
            userExeResult.setInput(input);
            userExeResult.setOutput(output);
            userExeResult.setExeOutput(exeOutput);
            userExeResultList.add(userExeResult);
            if (!output.equals(exeOutput)) {
                passed = false;
            }
        }
        return passed;
    }

    /**
     * 维护数据库中
     *
     * @param judgeSubmitDTO 用户输入的代码数据
     * @param userQuestionResultVO 返回给前端的数据
     */
    private void saveUserSubmit(JudgeSubmitDTO judgeSubmitDTO, UserQuestionResultVO userQuestionResultVO) {
        UserSubmit userSubmit = new UserSubmit();
        BeanUtil.copyProperties(userQuestionResultVO, userSubmit);
        userSubmit.setUserId(judgeSubmitDTO.getUserId());
        userSubmit.setQuestionId(judgeSubmitDTO.getQuestionId());
        userSubmit.setExamId(judgeSubmitDTO.getExamId());
        userSubmit.setProgramType(judgeSubmitDTO.getProgramType());
        userSubmit.setUserCode(judgeSubmitDTO.getUserCode());
        // 先把之前的答题数据给删除
        userSubmitMapper.delete(new LambdaQueryWrapper<UserSubmit>()
                .eq(UserSubmit::getUserId, judgeSubmitDTO.getUserId())
                .eq(UserSubmit::getQuestionId, judgeSubmitDTO.getQuestionId())
                .isNull(judgeSubmitDTO.getExamId() == null, UserSubmit::getExamId)
                .eq(judgeSubmitDTO.getExamId() != null, UserSubmit::getExamId, judgeSubmitDTO.getExamId())
        );
        // 然后直接插入就行了
        userSubmitMapper.insert(userSubmit);
    }
}
