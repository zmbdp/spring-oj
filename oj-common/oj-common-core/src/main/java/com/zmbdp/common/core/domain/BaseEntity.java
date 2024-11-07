package com.zmbdp.common.core.domain;

import lombok.Data;

import java.time.LocalDateTime;
// 为了方便我们写代码，因为很多地方都要用到这几个属性，所以直接提取出来
@Data
public class BaseEntity {
    private Long createBy; // 创建人
    private LocalDateTime createTime; // 创建时间
    private Long updateBy; // 修改人
    private LocalDateTime updateTime; // 修改时间
}
