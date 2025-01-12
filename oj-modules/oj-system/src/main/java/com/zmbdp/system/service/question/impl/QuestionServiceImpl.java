package com.zmbdp.system.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.system.domain.question.Question;
import com.zmbdp.system.domain.question.dto.QuestionAddDTO;
import com.zmbdp.system.domain.question.dto.QuestionEditDTO;
import com.zmbdp.system.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.system.domain.question.es.QuestionES;
import com.zmbdp.system.domain.question.vo.QuestionDetailVO;
import com.zmbdp.system.elasticsearch.QuestionRepository;
import com.zmbdp.system.manager.QuestionCacheManager;
import com.zmbdp.system.mapper.question.QuestionMapper;
import com.zmbdp.system.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends BaseService implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionCacheManager questionCacheManager;

    /**
     * 查询题目列表 service 层
     *
     * @param questionQueryDTO 要求参数
     * @return 题目列表
     */
    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        String excludeIdStr = questionQueryDTO.getExcludeIdStr();
        if (StrUtil.isNotEmpty(excludeIdStr)) {
            // 如果说存在的话就把这些题目 id 分割出来
            String[] excludeIdArr = excludeIdStr.split(Constants.SPLIT_SEM);
            Set<Long> excludeIdSet = Arrays.stream(excludeIdArr)
                    .map(Long::valueOf) // 在 map 中 把 string 类型的转换成为 long 型的数据
                    .collect(Collectors.toSet()); // 然后再去重
            questionQueryDTO.setExcludeIdSet(excludeIdSet);
        }
        PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize()); // 设置页面也每页记录数
        return getTableDataInfo(questionMapper.selectQuestionList(questionQueryDTO));
    }

    /**
     * 添加题目 service 层
     *
     * @param questionAddDTO 需要添加的题目信息
     * @return 是否添加成功
     */
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
        int insert = questionMapper.insert(question);
        if (insert <= 0) {
            return Result.fail();
        }
        // 然后得维护 es
        QuestionES questionES = new QuestionES();
        BeanUtil.copyProperties(question, questionES);
        questionRepository.save(questionES);
        // 还得放到缓存里面
        questionCacheManager.addCache(question.getQuestionId());
        return Result.success();
    }

    /**
     * 题目详情查询 service 层
     *
     * @param questionId 需要查询的题目的 id
     * @return 题目的信息
     */
    @Override
    public Result<QuestionDetailVO> detail(Long questionId) {
        // 题目详情查询
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            // 说明资源不存在
            return Result.fail(ResultCode.FAILED_NOT_EXISTS);
        }
        // 存在的话进行转换，转换成 VO 对象返回
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        BeanUtil.copyProperties(question, questionDetailVO);
        return Result.success(questionDetailVO);
    }

    /**
     * 编辑题目 service 层
     *
     * @param questionEditDTO 编辑好的题目信息
     * @return 是否成功
     */
    @Override
    public Result<Void> edit(QuestionEditDTO questionEditDTO) {
        // 先看看是否存在
        Question question = questionMapper.selectById(questionEditDTO.getQuestionId());
        if (question == null) {
            // 说明资源不存在
            return Result.fail(ResultCode.FAILED_NOT_EXISTS);
        }
        // 再看看有没有重复的
        // 如果说查到了有，并且这两个 id 不相等，就返回已存在
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .eq(Question::getTitle, questionEditDTO.getTitle())   // 查找相同 title 的记录
                .ne(Question::getQuestionId, questionEditDTO.getQuestionId()));  // 排除当前题目
        if (CollectionUtil.isNotEmpty(questions)) {
            // 说明数据库中题目存在了，不能添加
            return Result.fail(ResultCode.FAILED_ALREADY_EXISTS);
        }

        question.setTitle(questionEditDTO.getTitle());
        question.setDifficulty(questionEditDTO.getDifficulty());
        question.setTimeLimit(questionEditDTO.getTimeLimit());
        question.setSpaceLimit(questionEditDTO.getSpaceLimit());
        question.setContent(questionEditDTO.getContent());
        question.setQuestionCase(questionEditDTO.getQuestionCase());
        question.setDefaultCode(questionEditDTO.getDefaultCode());
        question.setMainFuc(questionEditDTO.getMainFuc());

        QuestionES questionES = new QuestionES();
        BeanUtil.copyProperties(question, questionES);
        questionRepository.save(questionES);
        return toResult(questionMapper.updateById(question));
    }

    /**
     * 删除题目 service 层
     *
     * @param questionId 需要删除的题目的 id
     * @return 是否删除成功
     */
    @Override
    public Result<Void> delete(Long questionId) {
        // 先查询是否存在
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            // 说明资源不存在
            return Result.fail(ResultCode.FAILED_NOT_EXISTS);
        }
        // 存在的话就删除
        questionRepository.deleteById(questionId); // 维护 es
        // 维护 redis
        questionCacheManager.deleteCache(questionId);
        return toResult(questionMapper.deleteById(questionId));
    }
}
