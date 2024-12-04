package com.zmbdp.system.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.system.domain.user.User;
import com.zmbdp.system.domain.user.dto.UserQueryDTO;
import com.zmbdp.system.domain.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<UserVO> selectUserList(UserQueryDTO userQueryDTO);
}
