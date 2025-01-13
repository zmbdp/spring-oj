package com.zmbdp.friend.domain.message.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class MessageTextVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long textId;

    private String messageTitle;

    private String messageContent;
}
