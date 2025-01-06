package com.zmbdp.friend.mapper.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.friend.domain.message.MessageText;
import com.zmbdp.friend.domain.message.vo.MessageTextVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageTextMapper extends BaseMapper<MessageText> {

    List<MessageTextVO> selectUserMsgList(Long userId);
}
