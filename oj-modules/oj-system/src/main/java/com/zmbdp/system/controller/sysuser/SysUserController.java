package com.zmbdp.system.controller.sysuser;

import cn.hutool.core.util.StrUtil;
import com.zmbdp.common.core.constants.HttpConstants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.vo.LoginUserVO;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.system.domain.sysuser.dto.LoginDTO;
import com.zmbdp.system.domain.sysuser.dto.SysUserSaveDTO;
import com.zmbdp.system.domain.sysuser.vo.SysUserVO;
import com.zmbdp.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sysUser")
@Tag(name = "管理员接口")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * @param loginDTO 管理员输入的用户名和密码
     * @return 登录成功与否，0- 成功 ，-1 -失败 -2 -未登录 ||
     * 如果失败说出原因
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "根据信息管理员的校验身份")
    @ApiResponse(responseCode = "1000", description = "登录成功")
    @ApiResponse(responseCode = "3001", description = "未授权")
    @ApiResponse(responseCode = "3103", description = "用户或密码错误")
    @ApiResponse(responseCode = "3105", description = "用户名或密码为输入")
    public Result<String> login(@RequestBody(required = false) @Validated LoginDTO loginDTO) {
        String userAccount = loginDTO.getUserAccount();
        String password = loginDTO.getPassword();
        return sysUserService.login(userAccount, password);
    }

    // 新增
    @PostMapping("/add")
    @Operation(summary = "新增管理员", description = "根据提供的信息新增管理员") // 描述接口操作
    @ApiResponse(responseCode = "1000", description = "添加成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户已存在")
    public Result<Void> add(@RequestBody(required = false) @Validated SysUserSaveDTO sysUserSaveDTO) {
        return sysUserService.add(sysUserSaveDTO);
    }

    // 接口地址: /system/sysUser/info
    // 这是获取用户的昵称
    @GetMapping("/info")
    public Result<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        if (StringUtils.isEmpty(token)) {
            return Result.fail(ResultCode.ERROR);
        }
        return sysUserService.info(token);
    }

    // 管理员退出功能
    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        if (StringUtils.isEmpty(token)) {
            return Result.fail(ResultCode.ERROR);
        }
        return sysUserService.logout(token);
    }



    // 删除用户接口
    @DeleteMapping("/del/{userId}")
    @Operation(summary = "删除用户", description = "通过用户id删除用户")
    @Parameters(value = {
            // 表示这是路径参数，这是必填的
            @Parameter(name = "userId", in = ParameterIn.PATH, description = "用户ID") // 这个就是指定的 URL 上的参数，然后说明解释参数是干什么的写到接口文档中
    })
    @ApiResponse(responseCode = "1000", description = "成功删除用户")
    @ApiResponse(responseCode = "2000", description = "服务繁忙，请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户不存在")
    public Result<Void> delete(@PathVariable(required = false) Long userId) {
        if (userId == null) {
            // 这里就是未选择 userId，让管理员选择
        }
        System.out.println("sysDelete");
        // 这里是删除操作的实现
        return null;
    }

    // 查询用户详情接口
    @Operation(summary = "用户详情", description = "根据查询条件查询用户详情")
    @GetMapping("/detail")
    @Parameters(value = {
            // ParameterIn.QUERY: 表示这是个查询参数，queueString 里面的
            @Parameter(name = "userId", in = ParameterIn.QUERY, description = "用户ID"),
            @Parameter(name = "sex", in = ParameterIn.QUERY, description = "用户性别")
    })
    @ApiResponse(responseCode = "1000", description = "成功获取用户信息")
    @ApiResponse(responseCode = "2000", description = "服务繁忙，请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户不存在")
    public Result<SysUserVO> detail(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String sex
    ) {
        System.out.println("sysDetail");
        // 这里是查询操作的实现
        return null;
    }
}
