package com.zmbdp.friend.domain.question.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class QuestionVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId; // 题目 id

    private String title; // 标题

    private Integer difficulty; // 难度
}
