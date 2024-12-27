package com.zmbdp.judge.service.impl;

import com.zmbdp.judge.domain.SandBoxExecuteResult;
import com.zmbdp.judge.service.ISandboxService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SandboxServiceImpl implements ISandboxService {
    @Override
    public SandBoxExecuteResult exeJavaCode(String userCode, List<String> inputList) {
        return null;
    }
}
