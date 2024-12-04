package com.zmbdp.system.controller.user;

import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.user.dto.UserQueryDTO;
import com.zmbdp.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @RequestMapping("/list")
    public TableDataInfo list(@Validated UserQueryDTO userQueryDTO) {
        return userService.list(userQueryDTO);
    }
}
