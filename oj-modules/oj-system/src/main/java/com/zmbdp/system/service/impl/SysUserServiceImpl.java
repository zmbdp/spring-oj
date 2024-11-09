package com.zmbdp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.enums.UserIdentity;
import com.zmbdp.common.security.service.TokenService;
import com.zmbdp.system.domain.SysUser;
import com.zmbdp.system.mapper.SysUserMapper;
import com.zmbdp.system.service.ISysUserService;
import com.zmbdp.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

// service 类的实现类
@Service
@RefreshScope // 加上这个注解就算改变 nacos 上 secret 这里也能获取到新的 secret
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Result<String> login(String userAccount, String password) {
        // 1. 先通过账号查询用户信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper.
                select(SysUser::getUserId, SysUser::getPassword). // 只查询出这个账户的密码
                        eq(SysUser::getUserAccount, // 表示 数据库字段，这里实际上指向 SysUser 类中的 userAccount 属性，它映射到数据库的 user_account 字段。
                        userAccount)); // 传入的 userAccount 字段
        if (sysUser == null || !BCryptUtils.matchPassword(password, sysUser.getPassword())) {
            // 表示用户不存在或者说密码错误，一起输出了，增加安全性
            return Result.fail(ResultCode.FAILED_LOGIN);
        }
        // 表示全都对上了，生成一个 Token 并且在 redis 中存储好，把 token 返回给前端，
        // 后面携带这个 token，直接来验证
        String token = tokenService.createToken(sysUser.getUserId(), secret, UserIdentity.ADMIN.getValue());
        return Result.success(token);
    }
}
