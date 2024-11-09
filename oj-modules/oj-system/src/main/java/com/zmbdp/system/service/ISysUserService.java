package com.zmbdp.system.service;

import com.zmbdp.common.core.domain.Result;

// service 类的接口类
public interface ISysUserService {
    Result<String> login(String userAccount, String password);
}
