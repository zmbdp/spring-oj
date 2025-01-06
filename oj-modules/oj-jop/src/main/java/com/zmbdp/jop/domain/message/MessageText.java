package com.zmbdp.jop.domain.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmbdp.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("tb_message_text")
@EqualsAndHashCode(callSuper = true)
public class MessageText extends BaseEntity {

    @TableId(value = "TEXT_ID", type = IdType.ASSIGN_ID)
    private Long textId;

    private String messageTitle;

    private String messageContent;
}
