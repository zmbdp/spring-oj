package com.zmbdp.system.service;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.vo.LoginUserVO;
import com.zmbdp.system.domain.dto.SysUserSaveDTO;

// service 类的接口类
public interface ISysUserService {
    Result<String> login(String userAccount, String password);

    Result<Void> logout(String token);

    Result<LoginUserVO> info(String token);

    Result<Void> add(SysUserSaveDTO sysUserSaveDTO);
}
