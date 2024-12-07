package com.zmbdp.common.core.domain;

import lombok.Data;

@Data
public class LoginUser {
    // 1 -普通用户
    // 2 -管理员用户
    private Integer identity;

    private String nickName;// 用户昵称

    private String headImage; // 头像
}
