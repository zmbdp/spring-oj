package com.zmbdp.system.domain.question.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionAddDTO {
    /**
     * @NotNull：值不能为null。
     * @NotEmpty：字符串、集合或数组的值不能为空，即长度必须大于0。
     * @NotBlank：字符串的值不能为空白，即不能只包含空格。
     * @Size：字符串、集合或数组的大小是否在指定范围内。
     * @Min：数值的最小值。
     * @Max：数值的最大值。
     * @Pattern：字符串是否匹配指定的正则表达式。
     * @Email：字符串是否为有效的电子邮件地址。
     * @Future：日期是否为将来的日期。
     * @Past：日期是否为过去的日期。
     */
    @NotNull(message = "标题不能为空")
    private String title; // 标题
    @Min(value = 1, message = "难度最小为简单")
    @Max(value = 3, message = "难度最大为困难")
    @NotNull(message = "难度不能为空")
    private Integer difficulty; // 难度
    @Min(value = 0, message = "最少为0ms")
    @NotNull(message = "时间限制不能为空")
    private Long timeLimit; // 时间限制
    @Min(value = 0, message = "最少为0mb")
    @NotNull(message = "空间限制不能为空")
    private Long spaceLimit; // 空间限制
    @NotNull(message = "题目内容不能为空")
    private String content; // 题目内容
    @NotNull(message = "题目用例不能为空")
    private String questionCase; // 题目用例
    @NotNull(message = "题目用例不能为空")
    private String defaultCode; // 题目用例
    @NotNull(message = "main函数不能为空")
    private String mainFuc; // main 函数
}
