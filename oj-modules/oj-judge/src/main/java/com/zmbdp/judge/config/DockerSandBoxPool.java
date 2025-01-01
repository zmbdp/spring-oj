package com.zmbdp.judge.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.zmbdp.common.core.constants.JudgeConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class DockerSandBoxPool {

    private DockerClient dockerClient;

    private String sandboxImage;

    private String volumeDir;

    private Long memoryLimit;

    private Long memorySwapLimit;

    private Long cpuLimit;

    private int poolSize;

    private String containerNamePrefix;

    private BlockingQueue<String> containerQueue; // 阻塞队列的容器池

    private Map<String, String> containerNameMap;

    public DockerSandBoxPool(DockerClient dockerClient,
                             String sandboxImage,
                             String volumeDir, Long memoryLimit,
                             Long memorySwapLimit, Long cpuLimit,
                             int poolSize, String containerNamePrefix) {
        this.dockerClient = dockerClient;
        this.sandboxImage = sandboxImage;
        this.volumeDir = volumeDir;
        this.memoryLimit = memoryLimit;
        this.memorySwapLimit = memorySwapLimit;
        this.cpuLimit = cpuLimit;
        this.poolSize = poolSize;
        this.containerQueue = new ArrayBlockingQueue<>(poolSize);
        this.containerNamePrefix = containerNamePrefix;
        this.containerNameMap = new HashMap<>();
    }

    public void initDockerPool() { // 初始化容器池的
        log.info("--------  创建容器开始  -------");
        for (int i = 0; i < poolSize; i++) {
            createContainer(containerNamePrefix + "-" + i);
        }
        log.info("--------  创建容器结束  -------");
    }

    /**
     * 获取容器
     *
     * @return 容器 id
     */
    public String getContainer() {
        try {
            return containerQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返还容器
     *
     * @param containerId 要返还的容器的 id
     */
    public void returnContainer(String containerId) {
        containerQueue.add(containerId);
    }

    private void createContainer(String containerName) {
        List<Container> containerList = dockerClient.listContainersCmd().withShowAll(true).exec();
        if (!CollectionUtil.isEmpty(containerList)) {
            String names = JudgeConstants.JAVA_CONTAINER_PREFIX + containerName;
            for (Container container : containerList) {
                String[] containerNames = container.getNames();
                if (containerNames != null && containerNames.length > 0 && names.equals(containerNames[0])) {
                    if ("created".equals(container.getState()) || "exited".equals(container.getState())) {
                        // 如果是非启动状态就启动容器
                        dockerClient.startContainerCmd(container.getId()).exec();
                    }
                    // 添加到 容器池 中
                    containerQueue.add(container.getId());
                    containerNameMap.put(container.getId(), containerName);
                    return;
                }
            }
        }

        // 拉取镜像
        pullJavaEnvImage();
        // 创建容器  限制资源  控制权限
        HostConfig hostConfig = getHostConfig(containerName);
        CreateContainerCmd containerCmd = dockerClient
                .createContainerCmd(JudgeConstants.JAVA_ENV_IMAGE)
                .withName(containerName);
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        // 记录容器id
        String containerId = createContainerResponse.getId();
        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();
        containerQueue.add(containerId);
        containerNameMap.put(containerId, containerName);
    }

    private void pullJavaEnvImage() {
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> imageList = listImagesCmd.exec();
        for (Image image : imageList) {
            String[] repoTags = image.getRepoTags();
            if (repoTags != null && repoTags.length > 0 && sandboxImage.equals(repoTags[0])) {
                return;
            }
        }
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(sandboxImage);
        try {
            pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 限制资源  控制权限
    private HostConfig getHostConfig(String containerName) {
        HostConfig hostConfig = new HostConfig();
        // 设置挂载目录，指定用户代码路径
        String userCodeDir = createContainerDir(containerName);
        hostConfig.setBinds(new Bind(userCodeDir, new Volume(volumeDir)));
        // 限制 docker 容器使用资源
        hostConfig.withMemory(memoryLimit);
        hostConfig.withMemorySwap(memorySwapLimit);
        hostConfig.withCpuCount(cpuLimit);
        hostConfig.withNetworkMode("none");  // 禁用网络
        hostConfig.withReadonlyRootfs(true); // 禁止在root目录写文件
        return hostConfig;
    }

    /**
     * 获取指定容器的挂在路径
     *
     * @param containerId 容器 id
     * @return
     */
    public String getCodeDir(String containerId) {
        String containerName = containerNameMap.get(containerId);
        log.info("containerName：{}", containerName);
        return System.getProperty("user.dir") + File.separator + JudgeConstants.CODE_DIR_POOL + File.separator + containerName;
    }

    /**
     * 为每个容器，创建的指定挂载文件
     *
     * @param containerName 容器名字
     * @return 返回路径名
     */
    private String createContainerDir(String containerName) {
        // 一级目录  存放所有容器的挂载目录
        String codeDir = System.getProperty("user.dir") + File.separator + JudgeConstants.CODE_DIR_POOL;
        if (!FileUtil.exist(codeDir)) {
            FileUtil.mkdir(codeDir);
        }
        return codeDir + File.separator + containerName;
    }
}
