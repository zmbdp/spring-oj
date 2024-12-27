package com.zmbdp.judge.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.constants.JudgeConstants;
import com.zmbdp.judge.domain.SandBoxExecuteResult;
import com.zmbdp.judge.service.ISandboxService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SandboxServiceImpl implements ISandboxService {

    private String userCodeDir;

    private String userCodeFileName;

    /**
     * 判题逻辑
     *
     * @param userCode  用户代码
     * @param inputList 指定输入
     * @return 输出
     */
    @Override
    public SandBoxExecuteResult exeJavaCode(String userCode, List<String> inputList) {
        return null;
    }

    private void createUserCodeFile(Long userId, String userCode) {
        String examCodeDir = System.getProperty("user.dir") + File.separator + JudgeConstants.EXAM_CODE_DIR;
        if (!FileUtil.exist(examCodeDir)) {
            FileUtil.mkdir(examCodeDir); // 创建存放用户代码的目录
        }
        String time = LocalDateTimeUtil.format(LocalDateTime.now(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 拼接用户代码文件格式
        userCodeDir = examCodeDir + File.separator + userId + Constants.UNDERLINE_SEPARATOR + time;
        userCodeFileName = userCodeDir + File.separator + JudgeConstants.USER_CODE_JAVA_CLASS_NAME;
        FileUtil.writeString(userCode, userCodeFileName, Constants.UTF8);
    }
}
