package com.zmbdp.friend.controller.exam;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.friend.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseService {
    @Autowired
    private IExamService examService;

    // 获取竞赛列表
    @GetMapping("/semiLogin/list")
    public TableDataInfo list(@Validated ExamQueryDTO examQueryDTO) {
        return getTableDataInfo(examService.list(examQueryDTO));
    }

    // 从 redis 中获取数据
    @GetMapping("/semiLogin/redis/list")
    public TableDataInfo redisList(@Validated ExamQueryDTO examQueryDTO) {
        return examService.redisList(examQueryDTO);
    }

    // 获取首个题目的 id 接口
    @GetMapping("/getFirstQuestion")
    public Result<String> getFirstQuestion(Long examId) {
        // 代码逻辑：先获取竞赛中题目列表的顺序 --- 先从 redis（key：e:q:l:examId   value: questionIds） 中查询，查不到再查数据库
        // 再把排在第一位的 id 返回给前端
        return Result.success(examService.getFirstQuestion(examId));
    }

    // 获取竞赛上一题接口
    @GetMapping("/preQuestion")
    public Result<String> preQuestion(Long examId, Long questionId) {
        return Result.success(examService.preQuestion(examId, questionId));
    }

    // 获取竞赛下一题接口
    @GetMapping("/nextQuestion")
    public Result<String> nextQuestion(Long examId, Long questionId) {
        return Result.success(examService.nextQuestion(examId, questionId));
    }
}
