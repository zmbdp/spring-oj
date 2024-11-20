package com.zmbdp.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.core.domain.LoginUser;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.vo.LoginUserVO;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.enums.UserIdentity;
import com.zmbdp.common.security.service.TokenService;
import com.zmbdp.system.domain.sysuser.SysUser;
import com.zmbdp.system.domain.sysuser.dto.SysUserSaveDTO;
import com.zmbdp.system.mapper.SysUserMapper;
import com.zmbdp.system.service.ISysUserService;
import com.zmbdp.system.utils.BCryptUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

// service 类的实现类
@Service
@RefreshScope // 加上这个注解就算改变 nacos 上 secret 这里也能获取到新的 secret
public class SysUserServiceImpl extends BaseService implements ISysUserService {
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
                select(SysUser::getUserId, SysUser::getPassword, SysUser::getNickName). // 只查询出这个账户的密码
                        eq(SysUser::getUserAccount, // 表示 数据库字段，这里实际上指向 SysUser 类中的 userAccount 属性，它映射到数据库的 user_account 字段。
                        userAccount)); // 传入的 userAccount 字段
        if (sysUser == null || !BCryptUtils.matchPassword(password, sysUser.getPassword())) {
            // 表示用户不存在或者说密码错误，一起输出了，增加安全性
            return Result.fail(ResultCode.FAILED_LOGIN);
        }
        // 表示全都对上了，生成一个 Token 并且在 redis 中存储好，把 token 返回给前端，
        // 后面携带这个 token，直接来验证
        String token = tokenService.createToken(sysUser.getUserId(), sysUser.getNickName(),
                secret, UserIdentity.ADMIN.getValue());
        return Result.success(token);
    }

    /**
     * 在这里写添加管理员的 service
     *
     * @param sysUserSaveDTO 需要添加的数据对象
     * @return 成功返回 success，失败返回 fail
     */
    @Override
    public Result<Void> add(SysUserSaveDTO sysUserSaveDTO) {
        SysUser sysUser = new SysUser();
        // 先查一遍看看有没有
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        List<SysUser> selectUsers = sysUserMapper.selectList(queryWrapper.
                eq(SysUser::getUserAccount, sysUserSaveDTO.getUserAccount()));
        // isNotEmpty -> 不为空返回 true，说明有数据
        if (CollectionUtil.isNotEmpty(selectUsers)) {
            // 说明找到了，直接返回存在该数据就行了
            return Result.fail(ResultCode.ALED_USER_EXISTS);
        }
        String password = BCryptUtils.encryptPassword(sysUserSaveDTO.getPassword());
        // 然后再把密码给加密好放到 user 对象里面
        sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
        sysUser.setPassword(password);
        // 再添加到数据库中
        return toResult(sysUserMapper.insert(sysUser));
    }

    // 获取用户的信息
    @Override
    public Result<LoginUserVO> info(String token) {
        if (StringUtils.isEmpty(token)) {
            return Result.fail(ResultCode.ERROR);
        }
        LoginUser loginUser = tokenService.getLoginUser(token, secret);
        if (loginUser == null) {
            return Result.fail();
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        return Result.success(loginUserVO);
    }

    // 退出登录逻辑的 service 代码
    @Override
    public Result<Void> logout(String token) {
        if (StringUtils.isEmpty(token)) {
            return Result.fail(ResultCode.ERROR);
        }
        return tokenService.delLoginUser(token, secret) ? Result.success() : Result.fail();
    }
}
