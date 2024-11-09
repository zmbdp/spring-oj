package com.zmbdp.system.domain;

import lombok.Data;

@Data
public class LoginUser {
    // 1 -普通用户
    // 2 -管理员用户
    private Integer identity;
}
