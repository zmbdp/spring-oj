package com.zmbdp.friend.service.user;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.friend.domain.exam.dto.ExamQueryDTO;

public interface IUserExamService {
    Result<Void> enter(String token, Long examId);

    TableDataInfo list(ExamQueryDTO examQueryDTO);
}
