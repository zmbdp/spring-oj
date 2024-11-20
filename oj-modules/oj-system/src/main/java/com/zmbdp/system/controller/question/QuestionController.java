package com.zmbdp.system.controller.question;

import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.system.controller.question.dto.QuestionQueryDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "题目管理接口")
@RequestMapping("/question")
public class QuestionController {
    // 获取题目列表接口
    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
    }
}
