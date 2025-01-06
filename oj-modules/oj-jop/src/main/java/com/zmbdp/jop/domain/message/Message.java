package com.zmbdp.jop.domain.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmbdp.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("tb_message")
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntity {

    @TableId(value = "MESSAGE_ID", type = IdType.ASSIGN_ID)
    private Long messageId;

    private Long textId;

    private Long sendId;

    private Long recId;
}
