package com.zmbdp.system.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.system.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.system.domain.question.Question;
import com.zmbdp.system.domain.question.vo.QuestionVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper  extends BaseMapper<Question> {
    List<QuestionVO> selectQuestionList(QuestionQueryDTO questionQueryDTO);
}
