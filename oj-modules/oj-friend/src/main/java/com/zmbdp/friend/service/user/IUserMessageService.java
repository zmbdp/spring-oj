package com.zmbdp.friend.service.user;

import com.zmbdp.common.core.domain.PageQueryDTO;
import com.zmbdp.common.core.domain.TableDataInfo;

public interface IUserMessageService {
    TableDataInfo list(PageQueryDTO dto);
}
