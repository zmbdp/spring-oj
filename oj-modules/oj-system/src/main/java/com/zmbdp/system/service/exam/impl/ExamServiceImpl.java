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
import com.zmbdp.system.domain.exam.dto.ExamAddDTO;
import com.zmbdp.system.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.system.mapper.exam.ExamMapper;
import com.zmbdp.system.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamServiceImpl extends BaseService implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        // 获取题目列表 service 层
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize()); // 设置页面也每页记录数
        return getTableDataInfo(examMapper.selectExamList(examQueryDTO));
    }

    @Override
    public Result<Void> add(ExamAddDTO examAddDTO) {
        // 新增题目 service 层
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
        return toResult(examMapper.insert(BeanUtil.copyProperties(examAddDTO, Exam.class)));
    }
}
