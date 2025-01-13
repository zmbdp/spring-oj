package com.zmbdp.system.test.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ValidationDTO {

    @NotNull(message = "昵称不能为空")
    private String nickName;

    @NotEmpty(message = "用户账号不能为空")
    private String userAccount;

    @NotBlank(message = "用户密码不能为空")
    @Size(min = 5, max = 10, message = "密码长度不能少于6位，不能大于10位")
    private String password;

    @Min(value = 0, message = "年龄不能小于0岁")
    @Max(value = 60, message = "年龄不能大于60岁")
    private int age;

    @Email(message = "必须符合邮箱格式")
    private String email;

    @Pattern(
            regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$",
            message = "手机号码格式不正确"
    )
    private String phone;

    @Past(message = "开始日期必须是过去的日期")
    private LocalDate startDate;

    @Future(message = "结束日期必须是未来的日期")
    private LocalDate endDate;
}
