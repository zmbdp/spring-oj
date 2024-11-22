package com.zmbdp.system.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.security.exception.ServiceException;
import com.zmbdp.system.domain.question.Question;
import com.zmbdp.system.domain.question.dto.QuestionAddDTO;
import com.zmbdp.system.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.system.mapper.question.QuestionMapper;
import com.zmbdp.system.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService extends BaseService implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize()); // 设置页面也每页记录数
        return getTableDataInfo(questionMapper.selectQuestionList(questionQueryDTO));
    }

    @Override
    public Result<Void> add(QuestionAddDTO questionAddDTO) {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .eq(Question::getTitle, questionAddDTO.getTitle()));
        if (CollectionUtil.isNotEmpty(questions)) {
            // 说明数据库中题目存在了，不能添加
            return Result.fail(ResultCode.FAILED_ALREADY_EXISTS);
        }
        Question question = new Question();
        // 把 questionAddDTO 转换成 question 对象，这样的话才会有创建人这些字段
        BeanUtil.copyProperties(questionAddDTO, question);
        return toResult(questionMapper.insert(question));
    }
}
