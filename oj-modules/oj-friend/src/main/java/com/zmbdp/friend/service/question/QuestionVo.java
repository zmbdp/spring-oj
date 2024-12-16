package com.zmbdp.friend.service.question;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class QuestionVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long questionId;

    private String title;

    private Integer difficulty;
}
