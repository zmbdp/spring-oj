package com.zmbdp.system.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {
    @NotNull(message = "用户id不能为空")
    private Long userId;
    @NotNull(message = "用户状态不能为空")
    private Integer status;
}