package com.zmbdp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.system.domain.sysuser.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
