package com.zmbdp.system.service.exam;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.exam.dto.ExamAddDTO;
import com.zmbdp.system.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.system.domain.exam.dto.ExamQuestionAddDTO;
import com.zmbdp.system.domain.exam.vo.ExamDetailVO;

public interface IExamService {

    TableDataInfo list(ExamQueryDTO examQueryDTO);

    Result<String> add(ExamAddDTO examAddDTO);

    Result<Void> questionAdd(ExamQuestionAddDTO examQuestionAddDTO);

    Result<ExamDetailVO> detail(Long examId);
}
