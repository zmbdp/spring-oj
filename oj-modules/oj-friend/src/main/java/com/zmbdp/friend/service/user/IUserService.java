package com.zmbdp.friend.service.user;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.vo.LoginUserVO;
import com.zmbdp.friend.domain.user.dto.UserDTO;
import jakarta.validation.constraints.NotBlank;

public interface IUserService {
    Result<Void> sendCode(UserDTO userDTO);

    Result<String> codeLogin(@NotBlank(message = "请输入手机号") String phone, String code);

    Result<Void> logout(String token);

    Result<LoginUserVO> info(String token);
}