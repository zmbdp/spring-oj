package com.zmbdp.friend.service.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.friend.domain.question.Question;
import com.zmbdp.friend.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.friend.domain.question.es.QuestionES;
import com.zmbdp.friend.elasticsearch.QuestionRepository;
import com.zmbdp.friend.mapper.question.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl extends BaseService implements IQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    /**
     * c 端获取题目列表的 service 层
     *
     * @param questionQueryDTO 搜索参数
     * @return 符合搜素要求的题目数据
     */
    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        long count = questionRepository.count();
        if (count <= 0) {
            // 然后就要从数据库中查
            refreshQuestion();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest
                .of(questionQueryDTO.getPageNum() - 1, questionQueryDTO.getPageSize(), sort);
        // 同步过的话直接从 es 上面查询
        Integer difficulty = questionQueryDTO.getDifficulty();
        String keyword = questionQueryDTO.getKeyword();
        Page<QuestionES> questionESPage;
        if (difficulty == null && StringUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findAll(pageable);
        } else if (StrUtil.isEmpty(keyword)) {
            // 说明 difficulty 不为空
            questionESPage = questionRepository.findQuestionByDifficulty(difficulty, pageable);
        } else if (difficulty == null) {
            // 说明 keyword 不为空
            questionESPage = questionRepository.findByTitleOrContent(keyword, keyword, pageable);
        } else {
            // 说明两个都不为空，得两个都满足
            questionESPage = questionRepository.findByTitleOrContentAndDifficulty(keyword, keyword, difficulty, pageable);
        }
        long total = questionESPage.getTotalElements();
        if (total <= 0) {
            // 说明没查询任何数据
            return TableDataInfo.empty();
        }
        List<QuestionES> questionESList = questionESPage.getContent();
        List<QuestionVo> questionVoList = BeanUtil.copyToList(questionESList, QuestionVo.class);
        return TableDataInfo.success(questionVoList, total);
    }

    private void refreshQuestion() {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>());
        if (CollectionUtil.isEmpty(questions)) {
            return;
        }
        // 说明有数据，就得同步给 es
        List<QuestionES> questionES = BeanUtil.copyToList(questions, QuestionES.class);
        questionRepository.saveAll(questionES);
    }
}
