package com.zmbdp.system.service.user;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.user.dto.UserDTO;
import com.zmbdp.system.domain.user.dto.UserQueryDTO;

public interface IUserService {
    TableDataInfo list(UserQueryDTO userQueryDTO);

    Result<Void> updateStatus(UserDTO userDTO);
}
