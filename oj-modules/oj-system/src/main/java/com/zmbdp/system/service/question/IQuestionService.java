package com.zmbdp.system.service.question;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.question.dto.QuestionAddDTO;
import com.zmbdp.system.domain.question.dto.QuestionQueryDTO;


public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);

    Result<Void> add(QuestionAddDTO questionAddDTO);
}
