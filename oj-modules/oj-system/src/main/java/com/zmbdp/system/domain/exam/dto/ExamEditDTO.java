package com.zmbdp.system.domain.exam.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamEditDTO extends ExamAddDTO{
    private Long examId;
}
