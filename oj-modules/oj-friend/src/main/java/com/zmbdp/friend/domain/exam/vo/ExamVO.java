package com.zmbdp.friend.domain.exam.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long examId; // 竞赛 id

    private String title; // 竞赛标题

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 竞赛开始时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime; // 竞赛结束时间

    private boolean enter = false; //true: 已经报名  false 未报名
}
