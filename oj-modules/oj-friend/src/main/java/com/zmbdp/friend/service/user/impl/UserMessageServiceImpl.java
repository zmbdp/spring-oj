package com.zmbdp.friend.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.domain.PageQueryDTO;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.utils.ThreadLocalUtil;
import com.zmbdp.friend.domain.message.Message;
import com.zmbdp.friend.domain.message.vo.MessageTextVO;
import com.zmbdp.friend.manager.MessageCacheManager;
import com.zmbdp.friend.mapper.message.MessageMapper;
import com.zmbdp.friend.mapper.message.MessageTextMapper;
import com.zmbdp.friend.service.user.IUserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserMessageServiceImpl implements IUserMessageService {

    @Autowired
    private MessageCacheManager messageCacheManager;

    @Autowired
    private MessageTextMapper messageTextMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public TableDataInfo list(PageQueryDTO dto) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        // 先拿到缓存中的数据
        Long total = messageCacheManager.getListSize(userId);
        List<MessageTextVO> messageTextVOList;
        // 如果缓存中没有的话，就从数据库中拿
        if (total == null || total <= 0) {
            // 从数据库中查询我的竞赛列表
            PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
            // 从数据库中拿
            messageTextVOList = messageTextMapper.selectUserMsgList(userId);
            // 刷新缓存
            messageCacheManager.refreshCache(userId);
            total = new PageInfo<>(messageTextVOList).getTotal();
        } else {
            // 有的话直接从缓存中拿
            messageTextVOList = messageCacheManager.getMsgTextVOList(dto, userId);
        }
        if (CollectionUtil.isEmpty(messageTextVOList)) {
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(messageTextVOList, total);
    }

    /**
     * 删除消息 service 层
     *
     * @param textId 消息 id
     * @return 成功与否
     */
    @Override
    public Result<Void> delete(Long textId) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        // 先删除数据库的
        messageMapper.delete(new LambdaQueryWrapper<Message>().eq(Message::getTextId, textId));
        messageTextMapper.deleteById(textId);
        // 然后再删除缓存中的
        messageCacheManager.deleteCache(userId, textId);
        return Result.success();
    }
}
