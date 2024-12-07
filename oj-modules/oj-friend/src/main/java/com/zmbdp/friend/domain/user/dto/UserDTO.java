package com.zmbdp.friend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    @NotBlank(message = "请输入手机号")
    private String phone;
    private String code;
}
