package com.zmbdp.api.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class JudgeSubmitDTO {

    private Long userId;

    private Long examId;

    // 编程语言类型（0 java 1 C++）
    private Integer programType;

    private Long questionId;

    // 题目难度
    private Integer difficulty;

    // 时间限制 ms
    private Long timeLimit;

    // 空间限制 kb
    private Long spaceLimit;

    // 拼装完整的用户代码  用户提交的代码 + main函数
    private String userCode;

    // 输入数据
    private List<String> inputList;

    // 期望输出数据
    private List<String> outputList;
}
