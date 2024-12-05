package com.zmbdp.common.core.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    // 拉黑用户
    Block(0),
    // 正常用户
    Normal(1),
    ;

    private Integer value;

    UserStatus(Integer value) {
        this.value = value;
    }
}
