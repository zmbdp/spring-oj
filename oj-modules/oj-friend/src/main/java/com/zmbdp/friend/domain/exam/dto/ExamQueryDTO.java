package com.zmbdp.friend.domain.exam.dto;

import com.zmbdp.common.core.domain.PageQueryDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamQueryDTO extends PageQueryDTO {
    private String title; // 竞赛标题
    private String startTime; // 竞赛开始时间
    private String endTime; // 竞赛结束时间
    @NotNull(message = "类型不能为空")
    private Integer type; // 0-> 未完赛 ； 1-> 已经完了的比赛
}
