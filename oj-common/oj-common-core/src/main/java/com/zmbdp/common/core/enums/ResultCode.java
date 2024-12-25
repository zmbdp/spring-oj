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
    FAILED_PARAMS_VALIDATE(3002, "参数校验失败"),
    FAILED_NOT_EXISTS(3003, "资源不存在"),
    FAILED_ALREADY_EXISTS(3004, "资源已存在"),

    // 表示用户相关的操作
    ALED_USER_EXISTS(3101, "账号已存在，请重新输入"),
    FAILED_USER_NOT_EXISTS(3102, "账号不存在，请先进行注册"),
    FAILED_LOGIN(3103, "账号或密码错误，请重新输入"),
    FAILED_USER_BANNED(3104, "您已被列入黑名单，请联系管理员"),
    TITLE_CANNOT_BE_NULL(3105, "标题不能为空"),
    FAILED_USER_PHONE(3106, "您输入的手机号有误，请重新输入"),
    FAILED_FREQUENT(3107, "操作频繁，请稍后重试"),
    FAILED_TIME_LIMIT(3108, "当天请求次数已达到上限"),
    FAILED_SEND_CODE(3109, "验证码发送错误"),
    FAILED_INVALID_CODE(3110, "验证码无效"),
    FAILED_ERROR_CODE(3111, "验证码错误"),

    // 竞赛相关状态码
    EXAM_START_TIME_BEFORE_CURRENT_TIME(3201, "竞赛开始时间不能早于当前时间"),
    EXAM_START_TIME_AFTER_END_TIME(3202, "竞赛开始时间不能晚于竞赛结束时间"),
    EXAM_NOT_EXISTS(3203, "竞赛不存在"),
    EXAM_QUESTION_NOT_EXISTS(3204, "操作的题目不存在"),
    EXAM_STARTED(3205, "禁止操作已开始的竞赛"),
    EXAM_NOT_HAS_QUESTION(3206, "禁止发布无题目的竞赛"),
    EXAM_IS_FINISH(3207, "禁止操作已结束的比赛"),
    EXAM_IS_PUBLISH(3208, "禁止操作已发布的比赛"),

    USER_EXAM_HAS_ENTER(3301, "请勿重复报名"),

    FAILED_FILE_UPLOAD(3401, "文件上传失败"),
    FAILED_FILE_UPLOAD_TIME_LIMIT(3402, "当天上传图片数量超过上限"),
    FAILED_FILE_SIZE_EXCEEDED(3403, "上传文件大小超出限制，请上传小于 10MB 的文件"),

    FAILED_FIRST_QUESTION(3501, "当前题目已经是第一题了哦"),
    FAILED_LAST_QUESTION(3502, "当前题目已经是最后一题了哦"),

    FAILED_NOT_SUPPORT_PROGRAM(3601, "暂不支持此语言"),

    FAILED_RABBIT_PRODUCE(3701, "mq生产消息异常")
    ;


    private final int code;
    private final String msg;
}
