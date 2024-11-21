package com.zmbdp.common.core.domain;

import lombok.Data;

@Data
public class PageQueryDTO {
    private Integer pageNum = 1; // 第几页
    private Integer pageSize = 10; // 一页多少条
}
