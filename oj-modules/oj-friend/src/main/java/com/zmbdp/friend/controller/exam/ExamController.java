package com.zmbdp.friend.controller.exam;

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
}
