package com.zmbdp.system.domain.user.dto;

import com.zmbdp.common.core.domain.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQueryDTO {
    private Long userId;
    private String nickName;
}
