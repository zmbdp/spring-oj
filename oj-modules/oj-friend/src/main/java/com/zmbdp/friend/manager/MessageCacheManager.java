package com.zmbdp.friend.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.domain.PageQueryDTO;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.friend.domain.message.vo.MessageTextVO;
import com.zmbdp.friend.mapper.message.MessageTextMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageCacheManager {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MessageTextMapper messageTextMapper;

    public Long getListSize(Long userId) {
        String userMsgListKey = getUserMsgListKey(userId);
        return redisService.getListSize(userMsgListKey);
    }

    private String getUserMsgListKey(Long userId) {
        return CacheConstants.USER_MESSAGE_LIST + userId;
    }

    private String getMsgDetailKey(Long textId) {
        return CacheConstants.MESSAGE_DETAIL + textId;
    }

    /**
     * 刷新用户信息的缓存
     *
     * @param userId 用户 id
     */
    public void refreshCache(Long userId) {
        List<MessageTextVO> messageTextVOList = messageTextMapper.selectUserMsgList(userId);
        if (CollectionUtil.isEmpty(messageTextVOList)) {
            return;
        }
        List<Long> textIdList = messageTextVOList.stream().map(MessageTextVO::getTextId).toList();
        String userMsgListKey = getUserMsgListKey(userId);
        redisService.rightPushAll(userMsgListKey, textIdList);
        Map<String, MessageTextVO> messageTextVOMap = new HashMap<>();
        for (MessageTextVO messageTextVO : messageTextVOList) {
            messageTextVOMap.put(getMsgDetailKey(messageTextVO.getTextId()), messageTextVO);
        }
        redisService.multiSet(messageTextVOMap);
    }

    /**
     * 获取用户 message详情 数据
     *
     * @param dto 几页几个（参数）
     * @param userId 用户 id
     * @return 用户 message详情 列表
     */
    public List<MessageTextVO> getMsgTextVOList(PageQueryDTO dto, Long userId) {
        int start = (dto.getPageNum() - 1) * dto.getPageSize();
        int end = start + dto.getPageSize() - 1; //下标需要 -1
        String userMsgListKey = getUserMsgListKey(userId);
        List<Long> msgTextIdList = redisService.getCacheListByRange(userMsgListKey, start, end, Long.class);
        List<MessageTextVO> messageTextVOList = assembleMsgTextVOList(msgTextIdList);
        if (CollectionUtil.isEmpty(messageTextVOList)) {
            // 说明 redis 中数据可能有问题 从数据库中查数据并且重新刷新缓存
            messageTextVOList = getMsgTextVOListByDB(dto, userId); // 从数据库中获取数据
            refreshCache(userId);
        }
        return messageTextVOList;
    }

    /**
     * 删除消息的缓存信息
     *
     * @param textId 消息 id
     */
    public void deleteCache(Long userId, Long textId) {
        redisService.deleteObject(getMsgDetailKey(textId));
        redisService.removeForList(getUserMsgListKey(userId), textId);
    }

    /**
     * 组装信息
     *
     * @param msgTextIdList 信息列表
     * @return 组装好的 message 列表
     */
    private List<MessageTextVO> assembleMsgTextVOList(List<Long> msgTextIdList) {
        if (CollectionUtil.isEmpty(msgTextIdList)) {
            // 说明 redis 当中没数据 从数据库中查数据并且重新刷新缓存
            return null;
        }
        // 拼接 redis 当中 key 的方法 并且将拼接好的 key 存储到一个 list 中
        List<String> detailKeyList = new ArrayList<>();
        for (Long textId : msgTextIdList) {
            detailKeyList.add(getMsgDetailKey(textId));
        }
        List<MessageTextVO> messageTextVOList = redisService.multiGet(detailKeyList, MessageTextVO.class);
        CollUtil.removeNull(messageTextVOList);
        if (CollectionUtil.isEmpty(messageTextVOList) || messageTextVOList.size() != msgTextIdList.size()) {
            // 说明 redis 中数据有问题 从数据库中查数据并且重新刷新缓存
            return null;
        }
        return messageTextVOList;
    }

    private List<MessageTextVO> getMsgTextVOListByDB(PageQueryDTO dto, Long userId) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        return messageTextMapper.selectUserMsgList(userId);
    }
}
