package com.zmbdp.jop.handler.hotlist;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.jop.mapper.user.UserSubmitMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HotListXxlJob {

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private RedisService redisService;

    @XxlJob("hotQuestionListHandler")
    public void hotQuestionListHandler() {
        log.info("----- 题目热门列表统计开始 ------");
        // 从数据库中拿到，要设置分页这些参数
        PageHelper.startPage(Constants.HOST_QUESTION_LIST_START, Constants.HOST_QUESTION_LIST_END);
        // 把数据库中的数据给拿出来
        List<Long> hotQuestionIdList = userSubmitMapper.selectHostQuestionList();
        // 如果数据库中有数据再进去执行，这样就不要打印两遍日志了
        if (CollectionUtil.isNotEmpty(hotQuestionIdList)) {
            // 先删除这些数据，然后再刷新缓存
            // 删除数据，如果失败打印日志
            if (!deleteHotList()) {
                // 先获取当前线程的堆栈跟踪信息。
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                StackTraceElement caller = stackTrace[2];  // 获取直接调用者信息
                log.error("刷新热榜排行失败 - 出错位置: {}.{}({}:{})",
                        caller.getClassName(),
                        caller.getMethodName(),
                        caller.getFileName(),
                        caller.getLineNumber());
            }
            // 刷新缓存
            refreshHotList(hotQuestionIdList);
        }
        log.info("----- 题目热门列表统计结束 ------");
    }

    // 删除逻辑
    private boolean deleteHotList() {
        return redisService.deleteObject(CacheConstants.QUESTION_HOST_LIST);
    }

    // 刷新缓存逻辑
    private void refreshHotList(List<Long> hotQuestionIdList) {
        redisService.rightPushAll(CacheConstants.QUESTION_HOST_LIST, hotQuestionIdList);
    }
}
