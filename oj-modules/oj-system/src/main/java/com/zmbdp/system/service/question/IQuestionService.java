package com.zmbdp.system.service.question;

import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.question.dto.QueryQueryDTO;


public interface IQuestionService {
    TableDataInfo list(QueryQueryDTO questionQueryDTO);
}
