package com.zmbdp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// API 网关相关逻辑的开发
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OjGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjGatewayApplication.class, args);
    }
}
