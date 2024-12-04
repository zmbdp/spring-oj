package com.zmbdp.system.controller.user;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.user.dto.UserDTO;
import com.zmbdp.system.domain.user.dto.UserQueryDTO;
import com.zmbdp.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 获取用户列表
     * @param userQueryDTO 限定的参数
     * @return 合适的用户数据
     */
    @GetMapping("/list")
    public TableDataInfo list(@Validated UserQueryDTO userQueryDTO) {
        return userService.list(userQueryDTO);
    }

    /**
     * 拉黑或解禁功能
     * @param userDTO 用户数据
     * @return 是否成功
     */
    @PutMapping("/updateStatus")
    public Result<Void> updateStatus(@RequestBody @Validated UserDTO userDTO) {
        return userService.updateStatus(userDTO);
    }
}
