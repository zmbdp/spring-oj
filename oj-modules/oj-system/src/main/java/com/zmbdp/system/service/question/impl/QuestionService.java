package com.zmbdp.system.service.question.impl;

import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.system.domain.question.dto.QueryQueryDTO;
import com.zmbdp.system.mapper.question.QuestionMapper;
import com.zmbdp.system.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService extends BaseService implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public TableDataInfo list(QueryQueryDTO questionQueryDTO) {
        PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize()); // 设置页面也每页记录数
        return getTableDataInfo(questionMapper.selectQuestionList(questionQueryDTO));
    }
}
