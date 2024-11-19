package com.zmbdp.gateway.filter;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.constants.HttpConstants;
import com.zmbdp.common.core.domain.LoginUser;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.enums.UserIdentity;
import com.zmbdp.common.core.utils.JwtUtils;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.gateway.properties.IgnoreWhiteProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 网关鉴权
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    // 排除过滤的 uri 白名单地址，在 nacos 自行添加
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RedisService redisService;

    // 继承 GlobalFilter 接口，要执行的过滤器方法
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 直接获取到请求的接口地址
        String url = request.getURI().getPath();

        // 跳过不需要验证的路径，接口白名单里面的接口均不需要验证
        if (matches(url, ignoreWhite.getWhites())) { // 判断一下接口是否在白名单中，白名单从 nacos 上拿
            // 说明在白名单中，不需要卡着，直接走就行
            return chain.filter(exchange);
        }

        // 从 http 请求头中获取 token
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return unauthorizedResponse(exchange, "令牌不能为空");
        }

        Claims claims;
        try {
            claims = JwtUtils.getTokenMsg(token, secret); // 获取令牌中信息 解析 payload 中信息
            if (claims == null) {
                return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
            }
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }

        String userKey = JwtUtils.getUserKey(claims); // 获取 jwt 中的 key
        // 然后去 redis 里面找，没有就返回 false
        boolean isLogin = redisService.hasKey(getTokenKey(userKey));
        if (!isLogin) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }

        String userId = JwtUtils.getUserId(claims); // 判断 jwt 中的信息是否完整
        if (StrUtil.isEmpty(userId)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        // 然后再从 redis 中拿到这些 user 的数据
        LoginUser user = redisService.getCacheObject(getTokenKey(userKey), LoginUser.class);
        if (url.contains(HttpConstants.SYSTEM_URL_PREFIX) && // 如果这走的是管理员的 url
                !UserIdentity.ADMIN.getValue().equals(user.getIdentity())) { // 并且他又不是管理员用户的话
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        if (url.contains(HttpConstants.FRIEND_URL_PREFIX) && // 如果他走的是普通用户的 url
                !UserIdentity.ORDINARY.getValue().equals(user.getIdentity())) { // 但是他不是普通用户的话
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        return chain.filter(exchange);
    }

    /**
     * 查找指定 url 是否匹配指定匹配规则链表中的任意一个字符串
     *
     * @param url         指定 url
     * @param patternList 需要检查的匹配规则链表
     * @return 是否匹配
     */
    private boolean matches(String url, List<String> patternList) {
        if (StrUtil.isEmpty(url) || CollectionUtils.isEmpty(patternList)) {
            return false;
        }
        for (String pattern : patternList) {
            if (isMatch(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 url 是否与规则匹配
     * 匹配规则中：
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级，只能是 1 个，0 个都不刑;
     * ** 表示任意层路径，可以为 0 个;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的 url
     * @return 是否匹配
     */
    private boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    /**
     * 获取缓存 key
     */
    private String getTokenKey(String token) {
        return CacheConstants.LOGIN_TOKEN_KEY + token;
    }

    /**
     * 从请求头中获取请求 token
     */
    private String getToken(ServerHttpRequest request) {
        // HttpConstants.AUTHENTICATION 是请求头当中的一个身份认证的凭据
        String token = request.getHeaders().getFirst(HttpConstants.AUTHENTICATION);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        // HttpConstants.PREFIX 就是前端设置的令牌前缀，如果说包含 bearer 的话，就把前缀给删掉
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return token;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径: {}", exchange.getRequest().getPath());
        return webFluxResponseWriter(exchange.getResponse(), msg, ResultCode.FAILED_UNAUTHORIZED.getCode());
    }

    // 拼装 webflux 模型响应
    private Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String msg, int code) {
        // 设置浏览器的状态码为 200
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Result<?> result = Result.fail(code, msg);
        // 这里相当于把 result 对象转换为 JSON 字符串返回回去，
        // 然后将字符串转换为字节数组，将其封装为 DataBuffer 对象，这是 WebFlux 中写入响应的缓冲区格式
        // 就是说，把你需要返回的信息（验证失败），还有自定义的 状态码
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    // 继承 Ordered 接口，定义过滤器的优先级
    @Override
    public int getOrder() {
        return -200; // 值越小，越先被执行
    }
}