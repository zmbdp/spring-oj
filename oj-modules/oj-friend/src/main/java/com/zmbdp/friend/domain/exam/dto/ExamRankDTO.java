package com.zmbdp.friend.domain.exam.dto;

import com.zmbdp.common.core.domain.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamRankDTO extends PageQueryDTO {
    private Long examId;
}
