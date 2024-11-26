package com.zmbdp.system.domain.question.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class QuestionDetailVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId; // 题目 id
    private String title; // 标题
    private Integer difficulty; // 难度
    private Long timeLimit; // 时间限制
    private Long spaceLimit; // 空间限制
    private String content; // 题目内容
    private String questionCase; // 题目用例
    private String defaultCode; // 默认代码块
    private String mainFuc; // main 函数
}
