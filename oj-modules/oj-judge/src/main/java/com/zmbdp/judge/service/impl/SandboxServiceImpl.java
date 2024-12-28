package com.zmbdp.judge.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.constants.JudgeConstants;
import com.zmbdp.common.core.enums.CodeRunStatus;
import com.zmbdp.judge.callback.DockerStartResultCallback;
import com.zmbdp.judge.callback.StatisticsCallback;
import com.zmbdp.judge.domain.CompileResult;
import com.zmbdp.judge.domain.SandBoxExecuteResult;
import com.zmbdp.judge.service.ISandboxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RefreshScope
public class SandboxServiceImpl implements ISandboxService {

    @Value("${sandbox.docker.host:tcp://localhost:2375}")
    private String dockerHost;

    @Value("${sandbox.limit.memory:100000000}")
    private Long memoryLimit;

    @Value("${sandbox.limit.memory-swap:100000000}")
    private Long memorySwapLimit;

    @Value("${sandbox.limit.cpu:1}")
    private Long cpuLimit;

    @Value("${sandbox.limit.time:5}")
    private Long timeLimit;

    private DockerClient dockerClient;

    private String containerId;

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
    public SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList) {
        // 创建 Java 文件
        createUserCodeFile(userId, userCode);
        // 初始化 docker 沙箱
        initDockerSanBox();
        // 编译代码
        CompileResult compileResult = compileCodeByDocker();
        if (!compileResult.isCompiled()) {
            deleteContainer();
            deleteUserCodeFile();
            return SandBoxExecuteResult.fail(CodeRunStatus.COMPILE_FAILED, compileResult.getExeMessage());
        }
        // 说明编译成功，得执行代码了
        return executeJavaCodeByDocker(inputList);
    }

    /**
     * 把用户的代码写入到文件中
     *
     * @param userId   用户 id
     * @param userCode 用户代码
     */
    private void createUserCodeFile(Long userId, String userCode) {
        String examCodeDir = System.getProperty("user.dir") + File.separator + JudgeConstants.EXAM_CODE_DIR;
        // 如果不存在就创建这个目录
        if (!FileUtil.exist(examCodeDir)) {
            FileUtil.mkdir(examCodeDir); // 创建存放用户代码的目录
        }
        String time = LocalDateTimeUtil.format(LocalDateTime.now(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 拼接用户代码文件格式
        userCodeDir = examCodeDir + File.separator + userId + Constants.UNDERLINE_SEPARATOR + time;
        userCodeFileName = userCodeDir + File.separator + JudgeConstants.USER_CODE_JAVA_CLASS_NAME;
        FileUtil.writeString(userCode, userCodeFileName, Constants.UTF8);
    }

    /**
     * 初始化 docker 沙箱
     */
    private void initDockerSanBox() {
        DefaultDockerClientConfig clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();
        dockerClient = DockerClientBuilder
                .getInstance(clientConfig)
                .withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
                .build();
        // 拉取 Java 镜像
        pullJavaEnvImage();
        // 创建容器  限制资源  控制权限
        HostConfig hostConfig = getHostConfig();
        CreateContainerCmd containerCmd = dockerClient
                .createContainerCmd(JudgeConstants.JAVA_ENV_IMAGE)
                .withName(JudgeConstants.JAVA_CONTAINER_NAME);
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        // 记录容器 id
        containerId = createContainerResponse.getId();
        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();
    }

    // 拉取 java 执行环境镜像 需要控制只拉取一次
    private void pullJavaEnvImage() {
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> imageList = listImagesCmd.exec();
        for (Image image : imageList) {
            String[] repoTags = image.getRepoTags();
            if (repoTags != null && repoTags.length > 0 && JudgeConstants.JAVA_ENV_IMAGE.equals(repoTags[0])) {
                return;
            }
        }
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(JudgeConstants.JAVA_ENV_IMAGE);
        try {
            pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 限制资源   控制权限
    private HostConfig getHostConfig() {
        HostConfig hostConfig = new HostConfig();
        // 设置挂载目录，指定用户代码路径
        hostConfig.setBinds(new Bind(userCodeDir, new Volume(JudgeConstants.DOCKER_USER_CODE_DIR)));
        // 限制docker容器使用资源
        hostConfig.withMemory(memoryLimit);
        hostConfig.withMemorySwap(memorySwapLimit);
        hostConfig.withCpuCount(cpuLimit);
        hostConfig.withNetworkMode("none"); // 禁用网络
        hostConfig.withReadonlyRootfs(true); // 禁止在 root 目录写文件
        return hostConfig;
    }

    // 编译
    // 的使用 docker 编译
    private CompileResult compileCodeByDocker() {
        // 先执行 Java 代码
        String cmdId = createExecCmd(JudgeConstants.DOCKER_JAVAC_CMD, null, containerId);
        DockerStartResultCallback resultCallback = new DockerStartResultCallback();
        CompileResult compileResult = new CompileResult();
        try {
            dockerClient.execStartCmd(cmdId)
                    .exec(resultCallback)
                    .awaitCompletion();
            if (CodeRunStatus.FAILED.equals(resultCallback.getCodeRunStatus())) {
                compileResult.setCompiled(false);
                compileResult.setExeMessage(resultCallback.getErrorMessage());
            } else {
                compileResult.setCompiled(true);
            }
            return compileResult;
        } catch (InterruptedException e) {
            // 此处可以直接抛出 已做统一异常处理  也可再做定制化处理
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行代码逻辑
     *
     * @param inputList 输入列表
     * @return
     */
    private SandBoxExecuteResult executeJavaCodeByDocker(List<String> inputList) {
        List<String> outList = new ArrayList<>(); //记录输出结果
        long maxMemory = 0L;  //最大占用内存
        long maxUseTime = 0L; //最大运行时间
        // 执行用户代码
        for (String inputArgs : inputList) {
            String cmdId = createExecCmd(JudgeConstants.DOCKER_JAVA_EXEC_CMD, inputArgs, containerId);
            // 执行代码
            StopWatch stopWatch = new StopWatch(); // 执行代码后开始计时
            // 执行情况监控
            StatsCmd statsCmd = dockerClient.statsCmd(containerId); // 启动监控
            StatisticsCallback statisticsCallback = statsCmd.exec(new StatisticsCallback());
            stopWatch.start();
            DockerStartResultCallback resultCallback = new DockerStartResultCallback();
            try {
                dockerClient.execStartCmd(cmdId)
                        .exec(resultCallback)
                        .awaitCompletion(timeLimit, TimeUnit.SECONDS);
                if (CodeRunStatus.FAILED.equals(resultCallback.getCodeRunStatus())) {
                    // 未通过所有用例返回结果
                    return SandBoxExecuteResult.fail(CodeRunStatus.NOT_ALL_PASSED);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            stopWatch.stop(); // 结束时间统计
            statsCmd.close(); // 结束 docker 容器执行统计
            long userTime = stopWatch.getLastTaskTimeMillis(); // 执行耗时
            maxUseTime = Math.max(userTime, maxUseTime);       // 记录最大的执行用例耗时
            Long memory = statisticsCallback.getMaxMemory();
            if (memory != null) {
                maxMemory = Math.max(maxMemory, statisticsCallback.getMaxMemory()); // 记录最大的执行用例占用内存
            }
            outList.add(resultCallback.getMessage().trim());   // 记录正确的输出结果
        }
        deleteContainer(); // 删除容器
        deleteUserCodeFile(); // 清理文件

        return getSanBoxResult(inputList, outList, maxMemory, maxUseTime); // 封装结果
    }

    /**
     * 执行 Java 代码
     *
     * @param javaCmdArr
     * @param inputArgs
     * @param containerId
     * @return
     */
    private String createExecCmd(String[] javaCmdArr, String inputArgs, String containerId) {
        if (!StrUtil.isEmpty(inputArgs)) {
            // 当入参不为空时拼接入参
            String[] inputArray = inputArgs.split(" "); // 入参
            javaCmdArr = ArrayUtil.append(JudgeConstants.DOCKER_JAVA_EXEC_CMD, inputArray);
        }
        ExecCreateCmdResponse cmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(javaCmdArr)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .exec();
        return cmdResponse.getId();
    }

    /**
     * 封装结果
     *
     * @param inputList 输入列表
     * @param outList 输出列表
     * @param maxMemory 最大空间
     * @param maxUseTime 最大时间
     * @return 封装完成之后的结果
     */
    private SandBoxExecuteResult getSanBoxResult(List<String> inputList, List<String> outList, long maxMemory, long maxUseTime) {
        if (inputList.size() != outList.size()) {
            // 输入用例数量 不等于 输出用例数量  属于执行异常
            return SandBoxExecuteResult.fail(CodeRunStatus.NOT_ALL_PASSED, outList, maxMemory, maxUseTime);
        }
        return SandBoxExecuteResult.success(CodeRunStatus.SUCCEED, outList, maxMemory, maxUseTime);
    }

    /**
     * 删除 docker 容器
     */
    private void deleteContainer() {
        // 执行完成之后删除容器
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
        // 断开和 docker 连接
        try {
            dockerClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除代码文件
     */
    private void deleteUserCodeFile() {
        FileUtil.del(userCodeDir);
    }
}
