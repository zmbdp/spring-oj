package com.zmbdp.friend.controller.user;

import com.zmbdp.common.core.domain.PageQueryDTO;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.friend.service.user.IUserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/message")
public class UserMessageController{

    @Autowired
    private IUserMessageService userMessageService;

    /**
     * 返回用户消息的接口
     * @param dto 定义的参数
     * @return 该用户的所有消息
     */
    @GetMapping("/list")
    public TableDataInfo list(PageQueryDTO dto) {
        return userMessageService.list(dto);
    }
}