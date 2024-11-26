package com.zmbdp.system.domain.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamAddDTO {
    @NotBlank(message = "标题不能为空")
    private String title; // 竞赛标题

    @NotNull(message = "竞赛开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 竞赛开始时间

    @NotNull(message = "竞赛结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime; // 竞赛结束时间
}
