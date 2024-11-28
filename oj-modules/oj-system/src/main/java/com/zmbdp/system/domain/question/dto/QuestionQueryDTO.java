package com.zmbdp.system.domain.question.dto;

import com.zmbdp.common.core.domain.PageQueryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionQueryDTO extends PageQueryDTO {
    @NotBlank(message = "标题不能为空")
    private String title; // 标题
    @NotNull(message = "难度不能为空")
    private Integer difficulty; // 难度
}
