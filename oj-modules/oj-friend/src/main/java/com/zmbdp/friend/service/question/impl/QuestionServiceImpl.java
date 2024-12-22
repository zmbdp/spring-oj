package com.zmbdp.friend.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.friend.domain.question.Question;
import com.zmbdp.friend.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.friend.domain.question.es.QuestionES;
import com.zmbdp.friend.domain.question.vo.QuestionDetailVO;
import com.zmbdp.friend.elasticsearch.QuestionRepository;
import com.zmbdp.friend.manager.QuestionCacheManager;
import com.zmbdp.friend.mapper.question.QuestionMapper;
import com.zmbdp.friend.service.question.IQuestionService;
import com.zmbdp.friend.domain.question.vo.QuestionVo;
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

    @Autowired
    private QuestionCacheManager questionCacheManager;

    /**
     * c 端获取题目列表的 service 层
     *
     * @param questionQueryDTO 搜索参数
     * @return 符合搜素要求的题目数据
     */
    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        // 先从 es 里面查
        long count = questionRepository.count();
        if (count <= 0) {
            // 查不到就要从数据库中查
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

    /**
     * 获取题目信息的 service 层
     *
     * @param questionId 题目id
     * @return 题目的信息
     */
    @Override
    public QuestionDetailVO detail(Long questionId) {
        // 先从 es 中看看能不能查到
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        if (questionES != null) {
            // 如果能查到，直接赋给 vo
            BeanUtil.copyProperties(questionES, questionDetailVO);
            return questionDetailVO;
        }
        // 如果查不到，再看看数据库里面有没有
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            // 如果没有那就是真没有
            return null;
        }
        // 如果有的话同步 es 缓存
        refreshQuestion();
        // 然后再赋值给 vo
        BeanUtil.copyProperties(question, questionDetailVO);
        // 然后再返回
        return questionDetailVO;
    }

    /**
     * 获取上一题的 service 层
     *
     * @param questionId 题目id
     * @return 返回题目上一题的题目 id 给前端
     */
    @Override
    public String preQuestion(Long questionId) {
        // 先看看 redis 中是否有数据
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            // 如果没有就刷新缓存
            questionCacheManager.refreshCache();
        }
        return questionCacheManager.preQuestion(questionId).toString();
    }

    /**
     * 获取下一题的 service 层
     *
     * @param questionId 题目id
     * @return 返回题目下一题的题目 id 给前端
     */
    @Override
    public String nextQuestion(Long questionId) {
        // 先看看 redis 中是否有数据
        Long listSize = questionCacheManager.getListSize();
        if (listSize == null || listSize <= 0) {
            // 如果没有就刷新缓存
            questionCacheManager.refreshCache();
        }
        return questionCacheManager.nextQuestion(questionId).toString();
    }

    /**
     * es 缓存同步方法
     */
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
