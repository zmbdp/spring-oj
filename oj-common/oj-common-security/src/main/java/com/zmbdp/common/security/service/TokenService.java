package com.zmbdp.common.security.service;

import cn.hutool.core.lang.UUID;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.constants.JwtConstants;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.common.core.utils.JwtUtils;
import com.zmbdp.common.core.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    @Autowired
    private RedisService redisService;
    // 生成一个 token
    public String createToken(Long userId, String secret, Integer identity) {
        Map<String, Object> claims = new HashMap<>();
        String userKey = UUID.fastUUID().toString(); // 把 hutool 产生的 UUID 当作唯一主键
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtils.createToken(claims, secret);
        // 然后使用 redis 存储敏感信息
        String key = CacheConstants.LOGIN_TOKEN_KEY + userKey; // 然后拼接好当作 redis 的主键
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity); // 是管理员，所以设置成 2
        redisService.setCacheObject(key, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);
        return token;
    }
}
