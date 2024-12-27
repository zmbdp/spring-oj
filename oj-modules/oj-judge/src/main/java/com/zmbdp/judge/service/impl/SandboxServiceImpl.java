package com.zmbdp.judge.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
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
import com.zmbdp.judge.domain.CompileResult;
import com.zmbdp.judge.domain.SandBoxExecuteResult;
import com.zmbdp.judge.service.ISandboxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
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
        // 执行代码
        return executeJavaCodeByDocker(inputList);
    }

    /**
     * 把用户的代码写入到文件中
     *
     * @param userId 用户 id
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
        // 创建容器  限制资源   控制权限
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
}
