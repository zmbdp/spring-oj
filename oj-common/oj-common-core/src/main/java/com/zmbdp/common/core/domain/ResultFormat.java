package com.zmbdp.common.core.domain;

import com.zmbdp.common.core.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResultFormat<T> {

    @Schema(description = "自定义状态码")
    private Integer code;

    @Schema(description = "消息内容")
    private String msg;

    @Schema(description = "数据对象")
    private T data;

    // 成功的两个，登录成功的话不需要参数，其他获取信息成功的话需要 date 参数
    public static <T> ResultFormat<T> success() {
        return assembleResult(ResultCode.SUCCESS, null);
    }

    // 如果说成功，有 data 参数的话就搞这个
    public static <T> ResultFormat<T> success(T data) {
        return assembleResult(ResultCode.SUCCESS, data);
    }

    public static <T> ResultFormat<T> fail() {
        return assembleResult(ResultCode.FAILED, null);
    }

    // 如果说这个失败，有 message 参数的话就这么搞
    public static <T> ResultFormat<T> fail(ResultCode resultCode) {
        return assembleResult(resultCode, null);
    }

    private static <T> ResultFormat<T> assembleResult(ResultCode resultCode, T date) {
        ResultFormat<T> result = new ResultFormat<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setData(date);
        return result;
    }
}
