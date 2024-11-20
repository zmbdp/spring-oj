package com.zmbdp.system.domain.sysuser;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmbdp.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("tb_sys_user")
@EqualsAndHashCode(callSuper = true) // 为了让父类里面的属性也能考虑到
public class SysUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID) // 通过雪花算法生成主键 id
    private Long userId; // 主键
    private String nickName; // 用户昵称
    private String userAccount; // 账号
    private String password; // 密码
}
