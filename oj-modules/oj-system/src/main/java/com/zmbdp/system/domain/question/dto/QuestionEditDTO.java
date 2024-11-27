package com.zmbdp.system.domain.question.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionEditDTO extends QuestionAddDTO {
    @NotNull(message = "题目id不能为空")
    private Long questionId; // 题目 id
}
