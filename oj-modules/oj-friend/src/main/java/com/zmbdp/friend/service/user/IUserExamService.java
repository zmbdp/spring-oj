package com.zmbdp.friend.service.user;

import com.zmbdp.common.core.domain.Result;

public interface IUserExamService {
    Result<Void> enter(String token, Long examId);
}
