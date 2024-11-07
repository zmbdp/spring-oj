package com.zmbdp.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysUserVO {
    @Schema(description = "管理员昵称")
    private String userAccount;
    @Schema(description = "管理员用户名")
    private String nickName;
}
