package com.zmbdp.friend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.friend.domain.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
