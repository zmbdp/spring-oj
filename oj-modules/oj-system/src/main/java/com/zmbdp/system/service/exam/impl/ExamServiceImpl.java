package com.zmbdp.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.system.domain.exam.Exam;
import com.zmbdp.system.domain.exam.ExamQuestion;
import com.zmbdp.system.domain.exam.dto.ExamAddDTO;
import com.zmbdp.system.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.system.domain.exam.dto.ExamQuestionAddDTO;
import com.zmbdp.system.domain.exam.vo.ExamDetailVO;
import com.zmbdp.system.domain.question.Question;
import com.zmbdp.system.domain.question.vo.QuestionVO;
import com.zmbdp.system.mapper.exam.ExamMapper;
import com.zmbdp.system.mapper.exam.ExamQuestionMapper;
import com.zmbdp.system.mapper.question.QuestionMapper;
import com.zmbdp.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExamServiceImpl extends BaseService implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Override
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        // 获取题目列表 service 层
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize()); // 设置页面也每页记录数
        return getTableDataInfo(examMapper.selectExamList(examQueryDTO));
    }

    @Override
    public Result<String> add(ExamAddDTO examAddDTO) {
        // 新增无题目的竞赛 service 层
        // 先查一下是否存在，因为竞赛名称不能重复
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examAddDTO.getTitle()));
        if (CollectionUtil.isNotEmpty(examList)) {
            return Result.fail(ResultCode.FAILED_ALREADY_EXISTS);
        }
        // 判断时间的问题
        if (examAddDTO.getStartTime().isBefore(LocalDateTime.now())) {
            // 竞赛开始时间不能早于当前时间
            return Result.fail(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
        if (examAddDTO.getStartTime().isAfter(examAddDTO.getEndTime())) {
            // 竞赛开始时间不能晚于竞赛结束时间
            return Result.fail(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        return examMapper.insert(exam) > 0 ? Result.success(exam.getExamId().toString()) : Result.fail(ResultCode.ERROR);
    }

    @Override
    public Result<Void> questionAdd(ExamQuestionAddDTO examQuestionAddDTO) {
        // 新增竞赛里面的题目 service 层
        // 先查一下 竞赛 看看存不存在
        Exam exam = getExam(examQuestionAddDTO.getExamId());
        if (exam == null) {
            return Result.fail(ResultCode.EXAM_NOT_EXISTS);
        }
        // 然后再查询一下看看这些 题目 是否存在
        Set<Long> questionIdSet = examQuestionAddDTO.getQuestionIdSet();
        if (CollectionUtil.isEmpty(questionIdSet)) {
            // 先拿到这些数据，如果说没有的话也行
            return null;
        }
        // 看看这些题目存不存在
        List<Question> questionList = questionMapper.selectBatchIds(questionIdSet);
        if (CollectionUtil.isEmpty(questionList) || questionList.size() < examQuestionAddDTO.getQuestionIdSet().size()) {
            return Result.fail(ResultCode.EXAM_QUESTION_NOT_EXISTS);
        }

        return toResult(saveExamQuestion(questionIdSet, exam));
    }

    @Override
    public Result<ExamDetailVO> detail(Long examId) {
        // 获取竞赛信息的 service 层
        // 先获取到竞赛的 id
        ExamDetailVO examDetailVO = new ExamDetailVO();
        Exam exam = getExam(examId);
        if (exam == null) {
            return Result.fail(ResultCode.EXAM_NOT_EXISTS);
        }
        BeanUtil.copyProperties(exam, examDetailVO);
        List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getQuestionOrder));
        if (CollectionUtil.isEmpty(examQuestionList)) {
            return Result.success(examDetailVO);
        }
        List<Long> questionIdList = examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();
        // select * from tb_question question_id in(1, 2, 3)
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                .in(Question::getQuestionId, questionIdList));
        // 先把所有的题目放到 list 里面
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionList, QuestionVO.class);
        // 然后再把这个 list 放到返回的 VO 里面去
        examDetailVO.setExamQuestionList(questionVOList);
        return Result.success(examDetailVO);
    }

    /**
     * 批量添加到数据库中
     *
     * @param questionIdSet 需要添加的题目的集合
     * @param exam          竞赛的id
     * @return 添加成功 true，不成功 false
     */
    private boolean saveExamQuestion(Set<Long> questionIdSet, Exam exam) {
        int num = 1;
        for (Long x : questionIdSet) {
            // 存在的话直接添加到 竞赛题目关系表中
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId(exam.getExamId());
            examQuestion.setQuestionId(x);
            examQuestion.setQuestionOrder(num++);
            if (examQuestionMapper.insert(examQuestion) < 1) {
                return false;
            }
        }
        // 此时说明参数这些都没问题的，如果说添加成功的话就是成功，不成功的话就是服务器的问题了
        return true;
    }

    private Exam getExam(Long examId) {
        return examMapper.selectById(examId);
    }
}
