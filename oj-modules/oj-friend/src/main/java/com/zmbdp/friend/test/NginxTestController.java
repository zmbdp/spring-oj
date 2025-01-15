package com.zmbdp.friend.test;

import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.core.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Nginx 负载均衡测试控制器
 */
@RestController
@RequestMapping("/test/nginx")
@Slf4j
public class NginxTestController extends BaseService {

    /**
     * 测试负载均衡信息
     *
     * @return 成功响应
     */
    @GetMapping("/info")
    public Result<Void> info() {
        log.info("负载均衡测试");
        return Result.success();
    }
}
