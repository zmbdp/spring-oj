package com.zmbdp.system.domain.sysuser.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    /**
     * @NotNull：值不能为null。
     * @NotEmpty：字符串、集合或数组的值不能为空，即长度必须大于0。
     * @NotBlank：字符串的值不能为空白，即不能只包含空格。
     * @Size：字符串、集合或数组的大小是否在指定范围内。
     * @Min：数值的最小值。
     * @Max：数值的最大值。
     * @Pattern：字符串是否匹配指定的正则表达式。
     * @Email：字符串是否为有效的电子邮件地址。
     * @Future：日期是否为将来的日期。
     * @Past：日期是否为过去的日期。
     */
    @NotBlank(message = "用户名不能为空")
    private String userAccount;
    @NotBlank(message = "用户密码不能为空")
    private String password;
}
