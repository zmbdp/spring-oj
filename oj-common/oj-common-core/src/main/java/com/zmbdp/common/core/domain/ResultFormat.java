package com.zmbdp.common.core.domain;

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
}
