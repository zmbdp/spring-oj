package com.zmbdp.system.domain.question.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionEditDTO extends QuestionAddDTO {
    @NotNull(message = "题目id不能为空")
    private Long questionId; // 题目 id
}
