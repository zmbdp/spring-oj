package com.zmbdp.friend.service.question;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.friend.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.friend.domain.question.vo.QuestionDetailVO;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);

    Result<QuestionDetailVO> detail(Long questionId);

    Result<String> preQuestion(Long questionId);

    Result<String> nextQuestion(Long questionId);
}
