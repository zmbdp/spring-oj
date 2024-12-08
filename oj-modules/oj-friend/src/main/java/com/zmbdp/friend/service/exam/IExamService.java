package com.zmbdp.friend.service.exam;

import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.friend.domain.exam.dto.ExamQueryDTO;

public interface IExamService {
    TableDataInfo list(ExamQueryDTO examQueryDTO);

    TableDataInfo redisList(ExamQueryDTO examQueryDTO);
}
