package com.zmbdp.system.service;

import com.zmbdp.common.core.domain.ResultFormat;

// service 类的接口类
public interface ISysUserService {
    ResultFormat<String> login(String userAccount, String password);
}
