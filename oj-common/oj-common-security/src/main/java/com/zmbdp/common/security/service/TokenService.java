package com.zmbdp.common.security.service;

import cn.hutool.core.lang.UUID;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.constants.JwtConstants;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.common.core.utils.JwtUtils;
import com.zmbdp.common.core.domain.LoginUser;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenService {
    @Autowired
    private RedisService redisService;
    // 生成一个 token
    public String createToken(Long userId, String nickName, String secret, Integer identity, String headImage) {
        Map<String, Object> claims = new HashMap<>();
        String userKey = UUID.fastUUID().toString(); // 把 hutool 产生的 UUID 当作唯一主键
        claims.put(JwtConstants.LOGIN_USER_ID, userId);
        claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtils.createToken(claims, secret);
        // 然后使用 redis 存储敏感信息
        String redisKey = getRedisKey(userKey); // 然后拼接好当作 redis 的主键
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity); // 是管理员，所以设置成 2
        loginUser.setNickName(nickName);
        loginUser.setHeadImage(headImage);
        redisService.setCacheObject(redisKey, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);
        return token;
    }

    // 什么时候延长？ -> 首先肯定是在身份验证完成之后，这时候 redis 里面才会有 key，并且肯定是要在 controller 层之前
    public void extendToken(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if (userKey == null) {
            return;
        }
        // 然后拼接 redis 的 key
        String redisKey = getRedisKey(userKey);
        // 然后再从 redis 中拿到过期时间，小于三小时就延长
        Long expire = redisService.getExpire(redisKey, TimeUnit.MINUTES);
        if (expire != null && expire < CacheConstants.REFRESH_TIME) {
            // 然后就开始延长
            redisService.expire(redisKey, CacheConstants.EXP, TimeUnit.MINUTES);
        }
    }

    // 转换成 redis 中存储的 key
    private static String getRedisKey(String userKey) {
        return CacheConstants.LOGIN_TOKEN_KEY + userKey;
    }

    private String getUserKey(String token, String secret) {
        Claims claims;
        try {
            claims = JwtUtils.getTokenMsg(token, secret); // 获取令牌中信息 解析 payload 中信息
            if (claims == null) {
                log.error("解析 token: {} 出现异常", token);
                return null;
            }
        } catch (Exception e) {
            log.error("解析 token: {} 出现异常", token, e);
            return null;
        }
        return JwtUtils.getUserKey(claims); // 获取 jwt 中的 key
    }

    public LoginUser getLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if (userKey == null) {
            return null;
        }
        // 获取到 redis 当中的 key
        String redisKey = getRedisKey(userKey);
        return redisService.getCacheObject(redisKey, LoginUser.class);
    }

    public boolean delLoginUser(String token, String secret) {
        String userKey = getUserKey(token, secret);
        if (userKey == null) {
            return false;
        }
        // 然后再删除掉 redis 中的这个 key
        String redisKey = getRedisKey(userKey);
        return redisService.deleteObject(redisKey);
    }
}
