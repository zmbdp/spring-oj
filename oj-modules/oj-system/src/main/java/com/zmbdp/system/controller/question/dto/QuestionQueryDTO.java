package com.zmbdp.system.controller.question.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private String title; // 标题
    private Integer difficulty; // 难度
    private Integer pageNum; // 第几页
    private Integer pageSize; // 一页多少条
}
