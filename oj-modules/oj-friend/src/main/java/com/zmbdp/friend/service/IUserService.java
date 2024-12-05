package com.zmbdp.friend.service;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.friend.domain.dto.UserDTO;
import jakarta.validation.constraints.NotBlank;

public interface IUserService {
    Result<Void> sendCode(UserDTO userDTO);

    Result<String> codeLogin(@NotBlank(message = "请输入手机号") String phone, String code);
}
