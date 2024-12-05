package com.zmbdp.friend.test;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.message.service.AliSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController extends BaseService {

    @Autowired
    private AliSmsService aliSmsService;

    @GetMapping("/sendCode")
    public Result<Void> sendCode(String phone, String code) {
        return toResult(aliSmsService.sendMobileCode(phone, code));
    }
}
