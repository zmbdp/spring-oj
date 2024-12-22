package com.zmbdp.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.system.domain.exam.Exam;
import com.zmbdp.system.domain.exam.ExamQuestion;
import com.zmbdp.system.domain.exam.dto.ExamAddDTO;
import com.zmbdp.system.domain.exam.dto.ExamEditDTO;
import com.zmbdp.system.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.system.domain.exam.dto.ExamQuestionAddDTO;
import com.zmbdp.system.domain.exam.vo.ExamDetailVO;
import com.zmbdp.system.domain.question.Question;
import com.zmbdp.system.domain.question.vo.QuestionVO;
import com.zmbdp.system.manager.ExamCacheManager;
import com.zmbdp.system.mapper.exam.ExamMapper;
import com.zmbdp.system.mapper.exam.ExamQuestionMapper;
import com.zmbdp.system.mapper.question.QuestionMapper;
import com.zmbdp.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Autowired
    private ExamCacheManager examCacheManager;

    /**
     * 获取竞赛列表 service 层
     *
     * @param examQueryDTO 符合这些要求的竞赛
     * @return 这一页的数据
     */
    @Override
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize()); // 设置页面也每页记录数
        return getTableDataInfo(examMapper.selectExamList(examQueryDTO));
    }

    /**
     * 新增无题目的竞赛 service 层
     *
     * @param examAddDTO 新增竞赛的基本信息
     * @return 新增的这个竞赛的 id
     */
    @Override
    public Result<String> add(ExamAddDTO examAddDTO) {
        // 先查一下是否存在，因为竞赛名称不能重复
        Result<String> saveParams = checkExamSaveParams(examAddDTO, null);
        if (saveParams != null) {
            return saveParams;
        }
        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        return examMapper.insert(exam) > 0 ? Result.success(exam.getExamId().toString()) : Result.fail(ResultCode.ERROR);
    }

    /**
     * 新增竞赛里面的题目 service 层
     *
     * @param examQuestionAddDTO 新增竞赛的题目
     * @return 添加是否成功
     */
    @Override
    public Result<Void> questionAdd(ExamQuestionAddDTO examQuestionAddDTO) {
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
        // 校验时间
        Result<Void> validationResult = checkTime(exam);
        if (validationResult != null) {
            return validationResult;
        }
        // 如果已经发布就不允许添加
        if (Constants.TRUE.equals(exam.getStatus())) {
            return Result.fail(ResultCode.EXAM_IS_PUBLISH);
        }
        // 看看这些题目存不存在
        List<Question> questionList = questionMapper.selectBatchIds(questionIdSet);
        if (CollectionUtil.isEmpty(questionList) || questionList.size() < questionIdSet.size()) {
            return Result.fail(ResultCode.EXAM_QUESTION_NOT_EXISTS);
        }

        return toResult(saveExamQuestion(questionIdSet, exam));
    }

    /**
     * 删除竞赛中的题目 service 层
     *
     * @param examId     竞赛 id
     * @param questionId 需要删除的题目 id
     * @return 成功返回 success 失败返回 fail
     */
    @Override
    public Result<Void> questionDelete(Long examId, Long questionId) {
        // 先看看这个竞赛的数据存不存在
        Exam exam = getExam(examId);
        if (exam == null) {
            return Result.fail(ResultCode.EXAM_NOT_EXISTS);
        }
        // 校验时间
        Result<Void> validationResult = checkTime(exam);
        if (validationResult != null) {
            return validationResult;
        }
        if (Constants.TRUE.equals(exam.getStatus())) {
            return Result.fail(ResultCode.EXAM_IS_PUBLISH);
        }
        return toResult(
                examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                        .eq(ExamQuestion::getExamId, examId)
                        .eq(ExamQuestion::getQuestionId, questionId)
                ));
    }

    /**
     * 获取竞赛信息的 service 层
     *
     * @param examId 竞赛 id
     * @return 这个竞赛的信息
     */
    @Override
    public Result<ExamDetailVO> detail(Long examId) {
        // 先获取到竞赛的 id
        Exam exam = getExam(examId);
        if (exam == null) {
            return Result.fail(ResultCode.EXAM_NOT_EXISTS);
        }
        ExamDetailVO examDetailVO = new ExamDetailVO();
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
     * 编辑竞赛 service 层
     *
     * @param examEditDTO 修改之后的竞赛
     * @return 返回是否成功
     */
    @Override
    public Result<String> edit(ExamEditDTO examEditDTO) {
        // 先看看是否存在这个数据
        Exam exam = getExam(examEditDTO.getExamId());
        if (exam == null) {
            return Result.fail(ResultCode.EXAM_NOT_EXISTS);
        }
        // 并且还不允许编辑已发布的竞赛
        if (Constants.TRUE.equals(exam.getStatus())) {
            return Result.fail(ResultCode.EXAM_IS_PUBLISH);
        }
        // 判断一些参数，比如说标题，开始时间和结束时间
        Result<String> saveParams = checkExamSaveParams(examEditDTO, examEditDTO.getExamId());
        if (saveParams != null) {
            return saveParams;
        }
        // 然后再将 examEditDTO copy 到 exam 里
        BeanUtil.copyProperties(examEditDTO, exam);
        // 修改数据库中的数据
        return examMapper.updateById(exam) > 0 ? Result.success() : Result.fail(ResultCode.ERROR);
    }

    /**
     * 删除竞赛的 service 层
     *
     * @param examId 需要删除的题目的 id
     * @return 是否删除成功
     */
    @Override
    public Result<Void> delete(Long examId) {
        Exam exam = getExam(examId);
        if (exam == null) {
            return Result.fail(ResultCode.EXAM_NOT_EXISTS);
        }
        if (Constants.TRUE.equals(exam.getStatus())) {
            return Result.fail(ResultCode.EXAM_IS_PUBLISH);
        }
        // 校验时间
        Result<Void> validationResult = checkTime(exam);
        if (validationResult != null) {
            return validationResult;
        }
        return toResult(
                examMapper.deleteById(exam) +
                        examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                                .eq(ExamQuestion::getExamId, examId))
        );
    }

    /**
     * 竞赛发布功能 service 层
     *
     * @param examId 需要发布的竞赛 id
     * @return 是否成功
     */
    @Override
    public Result<Void> publish(Long examId) {
        Exam exam = getExam(examId);
        if (exam == null) {
            return Result.fail(ResultCode.EXAM_NOT_EXISTS);
        }
        // 校验时间
        Result<Void> validationResult = checkTime(exam);
        if (validationResult != null) {
            return validationResult;
        }
        Long count = examQuestionMapper
                .selectCount(new LambdaQueryWrapper<ExamQuestion>()
                        .eq(ExamQuestion::getExamId, examId));
        if (count == null || count <= 0) {
            // 说明没有题目，不允许发布
            return Result.fail(ResultCode.EXAM_NOT_HAS_QUESTION);
        }
        exam.setStatus(Constants.TRUE);
        // 添加到 redis 的缓存中
        examCacheManager.addCache(exam);
        return toResult(examMapper.updateById(exam));
    }

    /**
     * 撤销发布 service 层
     *
     * @param examId 需要撤销的竞赛的id
     * @return 是否撤销成功
     */
    @Override
    public Result<Void> cancelPublish(Long examId) {
        Exam exam = getExam(examId);
        if (exam == null) {
            return Result.fail(ResultCode.EXAM_NOT_EXISTS);
        }
        // 校验时间
        Result<Void> validationResult = checkTime(exam);
        if (validationResult != null) {
            return validationResult ;
        }
        exam.setStatus(Constants.FALSE);
        // 再从 redis 中删除这个竞赛的缓存
        examCacheManager.deleteCache(examId);
        return toResult(examMapper.updateById(exam));
    }

    /**
     * 参数校验
     *
     * @param examSaveDTO 竞赛的信息
     * @param examId      当前竞赛的 id
     * @return 是否正确
     */
    private Result<String> checkExamSaveParams(ExamAddDTO examSaveDTO, Long examId) {
        // 判断一些参数是否合理
        // 查标题是否存在一样的
        List<Exam> examList = examMapper
                .selectList(new LambdaQueryWrapper<Exam>()
                        .eq(Exam::getTitle, examSaveDTO.getTitle())
                        .ne(examId != null, Exam::getExamId, examId)
                );
        if (CollectionUtil.isNotEmpty(examList)) {
            return Result.fail(ResultCode.FAILED_ALREADY_EXISTS);
        }
        // 判断时间的问题
        if (examSaveDTO.getStartTime().isBefore(LocalDateTime.now())) {
            // 竞赛开始时间不能早于当前时间
            return Result.fail(ResultCode.EXAM_START_TIME_BEFORE_CURRENT_TIME);
        }
        if (examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())) {
            // 竞赛开始时间不能晚于竞赛结束时间
            return Result.fail(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
        return null;
    }

    /**
     * 判断时间，如果说开始时间早于现在时间，就不允许编辑
     *
     * @param exam 传过来的竞赛数据
     * @return 竞赛开始时间早于当前时间返回 true，说明未开赛
     */
    private Result<Void> checkTime(Exam exam) {
        // 判断竞赛的开始时间如果说早于现在的时间，说明开赛了，不能操作
        // 如果说早于就返回 true
        // 竞赛开始的时间 ---- 现在的时间
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            return Result.fail(ResultCode.EXAM_STARTED);
        }
        if (exam.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail(ResultCode.EXAM_IS_FINISH);
        }
        return null;
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

    /**
     * 查询竞赛的实现
     *
     * @param examId 需要查询的竞赛 id
     * @return 返回查询到的这个竞赛的基本信息
     */
    private Exam getExam(Long examId) {
        return examMapper.selectById(examId);
    }
}
