package com.zmbdp.friend.service.question;

import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.friend.domain.question.dto.QuestionQueryDTO;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);
}
