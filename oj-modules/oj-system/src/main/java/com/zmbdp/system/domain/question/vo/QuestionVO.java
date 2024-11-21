package com.zmbdp.system.domain.question.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionVO {
    private Long questionId; // 题目 id
    private String title; // 标题
    private Integer difficulty; // 难度
    private String createName; // 创建人
    private LocalDateTime createTime; // 创建时间
}
