package com.zmbdp.common.file.domain;

import lombok.Data;

@Data
public class OSSResult {

    private String name;

    /**
     * 对象状态：true成功，false失败
     */
    private boolean success;
}
