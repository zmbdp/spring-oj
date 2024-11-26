package com.zmbdp.system.service.exam;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.exam.dto.ExamAddDTO;
import com.zmbdp.system.domain.exam.dto.ExamQueryDTO;

public interface IExamService {

    TableDataInfo list(ExamQueryDTO examQueryDTO);

    Result<Void> add(ExamAddDTO examAddDTO);
}
