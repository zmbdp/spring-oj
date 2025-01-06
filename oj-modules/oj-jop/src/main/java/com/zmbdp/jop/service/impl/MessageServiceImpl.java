package com.zmbdp.jop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmbdp.jop.domain.message.Message;
import com.zmbdp.jop.mapper.message.MessageMapper;
import com.zmbdp.jop.service.IMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Override
    public boolean batchInsert(List<Message> messageList) {
        return saveBatch(messageList);
    }
}
