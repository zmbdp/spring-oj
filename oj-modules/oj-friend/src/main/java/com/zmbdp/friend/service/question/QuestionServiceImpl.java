package com.zmbdp.friend.service.question;

import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.friend.domain.question.QuestionQueryDTO;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl extends BaseService implements IQuestionService {

    /**
     * c 端获取题目列表的 service 层
     *
     * @param questionQueryDTO 搜索参数
     * @return 符合搜素要求的题目数据
     */
    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {

    }
}
