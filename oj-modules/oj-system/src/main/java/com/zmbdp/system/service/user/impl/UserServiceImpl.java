package com.zmbdp.system.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.system.domain.user.dto.UserQueryDTO;
import com.zmbdp.system.mapper.user.UserMapper;
import com.zmbdp.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public TableDataInfo list(UserQueryDTO userQueryDTO) {
        // 设置页面也每页记录数
        PageHelper.startPage(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        return getTableDataInfo(userMapper.selectUserList(userQueryDTO));
    }
}
