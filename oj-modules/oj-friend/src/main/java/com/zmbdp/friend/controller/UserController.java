package com.zmbdp.friend.controller;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.friend.domain.dto.UserDTO;
import com.zmbdp.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * C端用户登录获取验证码
     * @param userDTO 用户登录输入的参数
     * @return 成功与否
     */
    @PostMapping("/sendCode")
    public Result<Void> sendCode(@Validated @RequestBody UserDTO userDTO) {
        return userService.sendCode(userDTO);
    }

    @PostMapping("/code/login")
    public Result<String> codeLogin(@Validated @RequestBody UserDTO userDTO) {
        return userService.codeLogin(userDTO.getPhone(), userDTO.getCode());
    }
}
