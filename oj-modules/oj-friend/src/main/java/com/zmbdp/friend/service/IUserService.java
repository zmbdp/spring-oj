package com.zmbdp.friend.service;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.friend.domain.dto.UserDTO;

public interface IUserService {
    Result<Void> sendCode(UserDTO userDTO);
}
