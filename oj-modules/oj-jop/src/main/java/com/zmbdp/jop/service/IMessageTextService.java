package com.zmbdp.jop.service;

import com.zmbdp.jop.domain.message.MessageText;

import java.util.List;

public interface IMessageTextService {
    boolean batchInsert(List<MessageText> messageTextList);
}
