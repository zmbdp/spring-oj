package com.zmbdp.system.service.question;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.question.dto.QuestionAddDTO;
import com.zmbdp.system.domain.question.dto.QuestionEditDTO;
import com.zmbdp.system.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.system.domain.question.vo.QuestionDetailVO;


public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);

    Result<Void> add(QuestionAddDTO questionAddDTO);

    Result<QuestionDetailVO> detail(Long questionId);

    Result<Void> edit(QuestionEditDTO questionEditDTO);

    Result<Void> delete(Long questionId);
}
