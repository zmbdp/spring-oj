package com.zmbdp.system.service.exam;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.exam.dto.ExamAddDTO;
import com.zmbdp.system.domain.exam.dto.ExamEditDTO;
import com.zmbdp.system.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.system.domain.exam.dto.ExamQuestionAddDTO;
import com.zmbdp.system.domain.exam.vo.ExamDetailVO;

public interface IExamService {

    TableDataInfo list(ExamQueryDTO examQueryDTO);

    Result<String> add(ExamAddDTO examAddDTO);

    Result<Void> questionAdd(ExamQuestionAddDTO examQuestionAddDTO);

    Result<Void> questionDelete(Long examId, Long questionId);

    Result<ExamDetailVO> detail(Long examId);

    Result<String> edit(ExamEditDTO examEditDTO);

    Result<Void> delete(Long examId);

    Result<Void> publish(Long examId);

    Result<Void> cancelPublish(Long examId);
}
