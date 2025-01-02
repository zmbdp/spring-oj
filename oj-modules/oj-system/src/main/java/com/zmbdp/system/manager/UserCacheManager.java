package com.zmbdp.system.manager;

import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.system.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserCacheManager {

    @Autowired
    private RedisService redisService;

    public void updateStatus(Long userId, Integer status) {
        // 刷新用户缓存
        String userKey = getUserKey(userId);
        User user = redisService.getCacheObject(userKey, User.class);
        // 如果缓存没有就直接返回就行，会自动去数据库中查
        if (user == null) {
            return;
        }
        user.setStatus(status);
        // 再刷新到 redis 中
        redisService.setCacheObject(userKey, user);
        // 设置用户缓存有效期为 10 分钟
        redisService.expire(userKey, CacheConstants.USER_EXP, TimeUnit.MINUTES);
    }

    // u:d: 用户 id
    private String getUserKey(Long userId) {
        return CacheConstants.USER_DETAIL + userId;
    }
}