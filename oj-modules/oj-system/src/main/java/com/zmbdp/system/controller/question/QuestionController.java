package com.zmbdp.system.controller.question;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.domain.question.Question;
import com.zmbdp.system.domain.question.dto.QuestionAddDTO;
import com.zmbdp.system.domain.question.dto.QuestionEditDTO;
import com.zmbdp.system.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.system.domain.question.vo.QuestionDetailVO;
import com.zmbdp.system.service.question.IQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "题目管理接口")
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private IQuestionService questionService;

    // 获取题目列表接口
    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        return questionService.list(questionQueryDTO);
    }

    @PostMapping("/add")
    public Result<Void> add(@Validated @RequestBody QuestionAddDTO questionAddDTO) {
        return questionService.add(questionAddDTO);
    }

    // 编辑题目
    @PutMapping("/edit")
    public Result<Void> edit(@Validated @RequestBody QuestionEditDTO questionEditDTO) {
        return questionService.edit(questionEditDTO);
    }

    // 获取题目详情
    @GetMapping("/detail")
    public Result<QuestionDetailVO> detail(@RequestParam("questionId") Long questionId) {
        return questionService.detail(questionId);
    }

    // 删除题目
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam("questionId") Long questionId) {
        return questionService.delete(questionId);
    }
}
