package com.zmbdp.common.core.utils;

import com.zmbdp.common.core.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Map;

public class JwtUtils {
    /**
     * 生成令牌
     *
     * @param claims 数据
     * @param secret 密钥
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims, String secret) {
        return Jwts.builder()
                   .setClaims(claims)
                   .signWith(SignatureAlgorithm.HS512, secret)
                   .compact();
    }

    /**
     * 从令牌中获取数据
     *
     * @param token 令牌
     * @param secret 密钥
     * @return 数据
     */
    public static Claims getTokenMsg(String token, String secret) {
        return Jwts.parser()
                   .setSigningKey(secret)
                   .parseClaimsJws(token)
                   .getBody();
    }

    public static String getUserKey(Claims claims) {
        Object value = claims.get(JwtConstants.LOGIN_USER_KEY);
        return getStr(value);
    }

    public static String getUserId(Claims claims) {
        // 先拿到 userId，然后判断是否为空，不为空就返回 toString
        Object value = claims.get(JwtConstants.LOGIN_USER_ID);
        return getStr(value);
    }

    private static String getStr(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
