package com.zmbdp.judge.service;

import com.zmbdp.judge.domain.SandBoxExecuteResult;

import java.util.List;

public interface ISandboxService {
    SandBoxExecuteResult exeJavaCode(String userCode, List<String> inputList);
}
