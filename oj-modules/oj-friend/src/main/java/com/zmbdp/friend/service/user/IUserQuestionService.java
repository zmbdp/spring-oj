package com.zmbdp.friend.service.user;

import com.zmbdp.api.domain.vo.UserQuestionResultVO;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.friend.domain.user.dto.UserSubmitDTO;

public interface IUserQuestionService {
    Result<UserQuestionResultVO> submit(UserSubmitDTO submitDTO);
}
