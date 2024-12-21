package com.zmbdp.friend.controller.user;

import cn.hutool.core.util.StrUtil;
import com.zmbdp.common.core.constants.HttpConstants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.vo.LoginUserVO;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.friend.domain.user.dto.UserDTO;
import com.zmbdp.friend.domain.user.dto.UserUpdateDTO;
import com.zmbdp.friend.domain.user.vo.UserVO;
import com.zmbdp.friend.service.user.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * C端用户登录获取验证码
     *
     * @param userDTO 用户登录输入的参数
     * @return 成功与否
     */
    @PostMapping("/sendCode")
    public Result<Void> sendCode(@Validated @RequestBody UserDTO userDTO) {
        return userService.sendCode(userDTO);
    }

    /**
     * C端用户登录功能
     *
     * @param userDTO 传过来的参数
     * @return 登录是否成功
     */
    @PostMapping("/code/login")
    public Result<String> codeLogin(@Validated @RequestBody UserDTO userDTO) {
        return userService.codeLogin(userDTO.getPhone(), userDTO.getCode());
    }

    /**
     * C端用户退出功能
     *
     * @param token 令牌
     * @return 是否退出成功
     */
    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        if (StringUtils.isEmpty(token)) {
            return Result.fail(ResultCode.ERROR);
        }
        return userService.logout(token);
    }

    /**
     * 获取用户当前信息
     *
     * @param token 用户的 token
     * @return 用户的信息
     */
    @GetMapping("/info")
    public Result<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        if (StringUtils.isEmpty(token)) {
            return Result.fail(ResultCode.ERROR);
        }
        return userService.info(token);
    }

    /**
     * 获取用户个人信息接口
     *
     * @return 用户的个人信息
     */
    @GetMapping("/detail")
    public Result<UserVO> detail() {
        return userService.detail();
    }

    /**
     * 编辑用户信息接口
     *
     * @param userUpdateDTO 用户修改之后的信息
     * @return 是否修改成功
     */
    @PutMapping("/edit")
    public Result<Void> edit(@RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.edit(userUpdateDTO);
    }

    /**
     * 更新头像
     * @param userUpdateDTO 更新好的数据
     * @return 是否更新完成
     */
    @PutMapping("/head-image/update")
    public Result<Void> updateHeadImage(@RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateHeadImage(userUpdateDTO.getHeadImage());
    }
}
