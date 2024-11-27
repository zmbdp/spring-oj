package com.zmbdp.system.domain.exam.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

@Getter
@Setter
public class ExamQuestionAddDTO {
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
    @NotNull(message = "竞赛id不能为空")
    private Long examId; // 竞赛id
    @NotEmpty(message = "题目id不能为空")
    private LinkedHashSet<Long> questionIdSet; // 题目id的集合
}
