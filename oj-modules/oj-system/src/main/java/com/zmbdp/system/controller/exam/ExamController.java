package com.zmbdp.system.controller.exam;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.exam.dto.ExamAddDTO;
import com.zmbdp.system.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.system.domain.exam.dto.ExamQuestionAddDTO;
import com.zmbdp.system.domain.exam.vo.ExamDetailVO;
import com.zmbdp.system.service.exam.IExamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam")
@Tag(name = "竞赛相关接口")
public class ExamController {

    @Autowired
    private IExamService examService;

    // 获取题目列表
    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return examService.list(examQueryDTO);
    }

    // 新增没有题目的竞赛
    @PostMapping("/add")
    public Result<String> add(@RequestBody @Validated ExamAddDTO examAddDTO) {
        return examService.add(examAddDTO);
    }

    // 新增有题目的竞赛
    @PostMapping("/question/add")
    public Result<Void> questionAdd(@RequestBody @Validated ExamQuestionAddDTO examQuestionAddDTO) {
        return examService.questionAdd(examQuestionAddDTO);
    }

    // 获取竞赛信息
    @GetMapping("/detail")
    public Result<ExamDetailVO> detail(@RequestParam("examId") Long examId) {
        return examService.detail(examId);
    }
}
