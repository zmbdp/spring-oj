package com.zmbdp.system.test;

import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private RedisService redisService;

    @RequestMapping("/redisTest")
    public String redisTest() {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setPassword("123456");
        redisService.setCacheObject("key", sysUser);
        SysUser result = redisService.getCacheObject("key", SysUser.class);
        return result.toString();
    }
}
