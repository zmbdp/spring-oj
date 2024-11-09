package com.zmbdp.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    // 成功的
    SUCCESS(1000, "操作成功"),

    // 服务器内部错误，友好提示
    ERROR(2000, "服务器繁忙，请稍后重试"),

    // 操作错误，但是服务器没有出现异常
    FAILED(3000, "操作失败"),
    FAILED_UNAUTHORIZED(3001, "未授权"),
    FAILED_UNAUTHORIZED_VALIDATE(3002, "参数校验失败"),
    FAILED_NOT_EXISTS(3003, "资源不存在"),
    FAILED_NOT_EXISTS_EXISTS(3004, "资源已存在"),

    // 表示用户相关的操作
    ALED_USER_EXISTS(3101, "账号已存在，请重新输入"),
    FAILED_USER_NOT_EXISTS(3102, "账号不存在，请先进行注册"),
    FAILED_LOGIN(3103, "账号或密码错误，请重新输入"),
    FAILED_USER_BLOCKED(3104, "您已被列入黑名单，请联系管理员"),
    FAILED_MISSING_CREDENTIALS(3105, "账号或密码未输入"),
    ;
    private int code;
    private String msg;
}
