package com.zmbdp.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "security.ignore")
// 从 nacos 上读取到所有前缀为 security.ignore 的白名单
public class IgnoreWhiteProperties {
    /**
     * 放行白名单配置，网关不校验此处的白名单
     */
    private List<String> whites = new ArrayList<>();
}