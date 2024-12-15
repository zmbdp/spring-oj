package com.zmbdp.friend.domain.question;

import com.zmbdp.common.core.domain.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionQueryDTO extends PageQueryDTO {
    private String keyword;

    private Integer difficulty;
}
