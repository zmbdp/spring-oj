package com.zmbdp.friend.controller.user;

import com.zmbdp.common.core.constants.HttpConstants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.friend.aspect.CheckUserStatus;
import com.zmbdp.friend.domain.exam.dto.ExamDTO;
import com.zmbdp.friend.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.friend.service.user.IUserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/exam")
public class UserExamController {

    @Autowired
    private IUserExamService userExamService;

    /**
     * 用户报名参加竞赛接口
     *
     * @param token   用户信息
     * @param examDTO 竞赛的相关信息
     * @return 是否成功
     */
    @CheckUserStatus // 使用 AOP 分别是不是拉黑用户
    @PostMapping("/enter")
    public Result<Void> enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody ExamDTO examDTO) {
        return userExamService.enter(token, examDTO.getExamId());
    }

    /**
     * 我的竞赛列表数据查询
     *
     * @param examQueryDTO 用户信息
     * @return 用户报名的所有竞赛
     */
    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return userExamService.list(examQueryDTO);
    }
}
