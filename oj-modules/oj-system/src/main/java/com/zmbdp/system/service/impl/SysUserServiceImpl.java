package com.zmbdp.system.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.constants.JwtConstants;
import com.zmbdp.common.core.domain.ResultFormat;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.enums.UserIdentity;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.common.security.utils.JwtUtils;
import com.zmbdp.system.domain.LoginUser;
import com.zmbdp.system.domain.SysUser;
import com.zmbdp.system.mapper.SysUserMapper;
import com.zmbdp.system.service.ISysUserService;
import com.zmbdp.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// service 类的实现类
@Service
@RefreshScope // 加上这个注解就算改变 nacos 上 secret 这里也能获取到新的 secret
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private RedisService redisService;

    @Override
    public ResultFormat<String> login(String userAccount, String password) {
        // 1. 先通过账号查询用户信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper.
                select(SysUser::getPassword). // 只查询出这个账户的密码
                        eq(SysUser::getUserAccount, // 表示 数据库字段，这里实际上指向 SysUser 类中的 userAccount 属性，它映射到数据库的 user_account 字段。
                        userAccount));// 传入的 userAccount 字段
        if (sysUser == null || !BCryptUtils.matchPassword(password, sysUser.getPassword())) {
            // 表示用户不存在或者说密码错误，一起输出了，增加安全性
            return ResultFormat.fail(ResultCode.FAILED_LOGIN);
        }
        // 表示全都对上了，生成一个 Token 返回给前端，后面携带这些信息，直接来验证
        Map<String, Object> claims = new HashMap<>();
        String userKey = UUID.fastUUID().toString(); // 把 hutool 产生的 UUID 当作唯一主键
        claims.put(JwtConstants.LOGIN_USER_ID, sysUser.getUserId());
        claims.put(JwtConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtils.createToken(claims, secret);
        // 然后使用 redis 存储敏感信息
        String key = CacheConstants.LOGIN_TOKEN_KEY + userKey; // 然后拼接好当作 redis 的主键
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(UserIdentity.ADMIN.getValue()); // 是管理员，所以设置成 2
        redisService.setCacheObject(key, loginUser, CacheConstants.EXP, TimeUnit.MINUTES);


        return ResultFormat.success(token);
    }
}
