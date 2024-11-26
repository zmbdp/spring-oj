package com.zmbdp.system.domain.exam.dto;

import com.zmbdp.common.core.domain.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamQueryDTO extends PageQueryDTO {
    private String title; // 竞赛标题
    private String startTime; // 竞赛开始时间
    private String endTime; // 竞赛结束时间
}
