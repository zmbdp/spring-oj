package com.zmbdp.common.security.interceptor;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.util.StringUtil;
import com.zmbdp.common.core.constants.HttpConstants;
import com.zmbdp.common.security.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在这里执行 token 的时间延长
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return true;
        }
        tokenService.extendToken(token, secret);
        return true;
    }

    // 从 request 中获取 token
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpConstants.AUTHENTICATION);
        if (StringUtil.isNotEmpty(token) && StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, "");
        }
        return token;
    }
}
