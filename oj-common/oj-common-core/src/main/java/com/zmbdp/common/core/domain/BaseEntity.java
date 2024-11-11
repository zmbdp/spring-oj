package com.zmbdp.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
// 为了方便我们写代码，因为很多地方都要用到这几个属性，所以直接提取出来
@Data
public class BaseEntity {

    /**
     * DEFAULT -默认不处理
     * INSERT -插入时填充字段
     * UPDATE -更新时填充字段
     * INSERT_UPDATE- 插入和更新时填充字段
     */

    @TableField(fill = FieldFill.INSERT) // 通过 myBatisPlus 注解自动填充创建人
    private Long createBy; // 创建人

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy; // 修改人

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 修改时间
}
