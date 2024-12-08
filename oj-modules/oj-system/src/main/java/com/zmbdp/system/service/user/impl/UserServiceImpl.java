package com.zmbdp.system.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.system.domain.user.User;
import com.zmbdp.system.domain.user.dto.UserDTO;
import com.zmbdp.system.domain.user.dto.UserQueryDTO;
import com.zmbdp.system.manager.UserCacheManager;
import com.zmbdp.system.mapper.user.UserMapper;
import com.zmbdp.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCacheManager userCacheManager;

    @Override
    public TableDataInfo list(UserQueryDTO userQueryDTO) {
        // 设置页面也每页记录数
        PageHelper.startPage(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        return getTableDataInfo(userMapper.selectUserList(userQueryDTO));
    }

    /**
     * 拉黑或解禁功能 service 层
     * @param userDTO 操作的用户
     * @return 成功与否
     */
    @Override
    public Result<Void> updateStatus(UserDTO userDTO) {
        // 先判断有没有这个人
        User user = userMapper.selectById(userDTO.getUserId());
        if (user == null) {
            return Result.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setStatus(userDTO.getStatus());
        // 再删除 redis 中关于这个人的数据的缓存
        userCacheManager.updateStatus(user.getUserId(), userDTO.getStatus());
        return toResult(userMapper.updateById(user));
    }
}
