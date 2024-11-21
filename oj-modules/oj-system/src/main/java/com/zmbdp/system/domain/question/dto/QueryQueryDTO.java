package com.zmbdp.system.domain.question.dto;

import com.zmbdp.common.core.domain.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryQueryDTO extends PageQueryDTO {
    private String title; // 标题
    private Integer difficulty; // 难度
}
