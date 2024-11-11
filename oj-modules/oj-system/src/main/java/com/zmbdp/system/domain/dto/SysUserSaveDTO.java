package com.zmbdp.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysUserSaveDTO {
    @Schema(description = "管理员用户名")
    private String userAccount;
    @Schema(description = "管理员密码")
    private String password;
}
