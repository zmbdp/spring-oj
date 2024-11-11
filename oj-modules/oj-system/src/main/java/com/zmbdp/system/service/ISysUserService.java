package com.zmbdp.system.service;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.system.domain.dto.SysUserSaveDTO;

// service 类的接口类
public interface ISysUserService {
    Result<String> login(String userAccount, String password);

    Result<Void> add(SysUserSaveDTO sysUserSaveDTO);
}
