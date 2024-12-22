package com.zmbdp.system.controller.exam;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.exam.dto.ExamAddDTO;
import com.zmbdp.system.domain.exam.dto.ExamEditDTO;
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

    // 获取竞赛列表
    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return examService.list(examQueryDTO);
    }

    // 新增没有题目的竞赛
    @PostMapping("/add")
    public Result<String> add(@RequestBody @Validated ExamAddDTO examAddDTO) {
        return examService.add(examAddDTO);
    }

    // 新增竞赛当中的题目
    @PostMapping("/question/add")
    public Result<Void> questionAdd(@RequestBody @Validated ExamQuestionAddDTO examQuestionAddDTO) {
        return examService.questionAdd(examQuestionAddDTO);
    }

    // 删除竞赛当中的题目
    @DeleteMapping("/question/delete")
    public Result<Void> questionDelete(Long examId, Long questionId) {
        return examService.questionDelete(examId, questionId);
    }

    // 获取竞赛信息
    @GetMapping("/detail")
    public Result<ExamDetailVO> detail(Long examId) {
        return examService.detail(examId);
    }

    // 编辑竞赛
    @PutMapping("/edit")
    public Result<String> edit(@RequestBody ExamEditDTO examEditDTO) {
        return examService.edit(examEditDTO);
    }

    // 删除竞赛
    @DeleteMapping("/delete")
    public Result<Void> delete(Long examId) {
        return examService.delete(examId);
    }


    // 发布功能
    @PutMapping("/publish")
    public Result<Void> publish(Long examId) {
        return examService.publish(examId);
    }

    // 撤销发布
    @PutMapping("/cancelPublish")
    public Result<Void> cancelPublish(Long examId) {
        return examService.cancelPublish(examId);
    }
}
