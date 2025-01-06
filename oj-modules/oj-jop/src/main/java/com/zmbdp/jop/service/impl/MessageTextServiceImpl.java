package com.zmbdp.jop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmbdp.jop.domain.message.MessageText;
import com.zmbdp.jop.mapper.message.MessageTextMapper;
import com.zmbdp.jop.service.IMessageTextService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageTextServiceImpl extends ServiceImpl<MessageTextMapper, MessageText> implements IMessageTextService {

    @Override
    public boolean batchInsert(List<MessageText> messageTextList) {
        return saveBatch(messageTextList);
    }
}
