package com.zmbdp.jop.service;

import com.zmbdp.jop.domain.message.Message;

import java.util.List;

public interface IMessageService {
    boolean batchInsert(List<Message> messageTextList);
}
