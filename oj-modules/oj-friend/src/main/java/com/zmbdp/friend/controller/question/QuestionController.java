package com.zmbdp.friend.controller.question;
import com.zmbdp.friend.domain.question.vo.QuestionVO;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.friend.domain.question.dto.QuestionQueryDTO;
import com.zmbdp.friend.domain.question.vo.QuestionDetailVO;
import com.zmbdp.friend.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private IQuestionService questionService;

    /**
     * 题目列表的 controller 层
     *
     * @param questionQueryDTO 搜索参数
     * @return 符合搜素要求的题目
     */
    @GetMapping("/semiLogin/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        return questionService.list(questionQueryDTO);
    }

    /**
     * 获取热榜排行接口
     *
     * @return 排行的数据
     */
    @GetMapping("/semiLogin/hotList")
    public Result<List<QuestionVO>> hotList() {
        return Result.success(questionService.hotList());
    }

    /**
     * 获取题目信息的接口
     *
     * @param questionId 题目id
     * @return 题目的信息
     */
    @GetMapping("/detail")
    public Result<QuestionDetailVO> detail(Long questionId) {
        return Result.success(questionService.detail(questionId));
    }

    /**
     * 获取上一题信息接口
     *
     * @param questionId 题目id
     * @return 上一题的题目信息
     */
    @GetMapping("/preQuestion")
    public Result<String> preQuestion(Long questionId) {
        return Result.success(questionService.preQuestion(questionId));
    }

    /**
     * 获取下一题信息接口
     *
     * @param questionId 题目id
     * @return 下一题的题目id
     */
    @GetMapping("/nextQuestion")
    public Result<String> nextQuestion(Long questionId) {
        return Result.success(questionService.nextQuestion(questionId));
    }
}
