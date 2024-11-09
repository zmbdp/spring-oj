package com.zmbdp.common.core.domain;

import com.zmbdp.common.core.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Result<T> {

    @Schema(description = "自定义状态码")
    private int code;

    @Schema(description = "消息内容")
    private String msg;

    @Schema(description = "数据对象")
    private T data;

    // 成功的两个，登录成功的话不需要参数，其他获取信息成功的话需要 date 参数
    public static <T> Result<T> success() {
        return assembleResult(ResultCode.SUCCESS, null);
    }

    // 如果说成功，有 data 参数的话就搞这个
    public static <T> Result<T> success(T data) {
        return assembleResult(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> fail() {
        return assembleResult(ResultCode.FAILED, null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return assembleResult(code, msg, null);
    }

    // 如果说这个失败，有 message 参数的话就这么搞
    /**
     * 指定错误码的返回格式
     * @param resultCode 指定的错误码
     * @return
     * @param <T>
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        // 错误的话就不返回数据了嘛，直接返回错误信息就好了
        return assembleResult(resultCode, null);
    }

    private static <T> Result<T> assembleResult(ResultCode resultCode, T date) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setData(date);
        return result;
    }

    private static <T> Result<T> assembleResult(int code, String msg, T date) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(date);
        return result;
    }
}
