package com.zmbdp.judge.domain;

import com.zmbdp.common.core.enums.CodeRunStatus;
import lombok.Data;

import java.util.List;

@Data
public class SandBoxExecuteResult {

    private CodeRunStatus runStatus;  // 执行结果

    private String exeMessage;   // 异常信息

    private List<String> outputList; // 执行结果

    private Long useMemory;  // 占用内存  kb

    private Long useTime;   // 消耗时间   ms

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus, String errorMsg) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setExeMessage(errorMsg);
        return result;
    }

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setExeMessage(runStatus.getMsg());
        return result;
    }

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus, List<String> outputList,
                                            Long useMemory, Long useTime) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setExeMessage(runStatus.getMsg());
        result.setOutputList(outputList);
        result.setUseMemory(useMemory);
        result.setUseTime(useTime);
        return result;
    }

    public static SandBoxExecuteResult success(CodeRunStatus runStatus, List<String> outputList,
                                            Long useMemory, Long useTime) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setOutputList(outputList);
        result.setUseMemory(useMemory);
        result.setUseTime(useTime);
        return result;
    }
}
