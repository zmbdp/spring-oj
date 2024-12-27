package com.zmbdp.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zmbdp.api.domain.UserExeResult;
import lombok.Data;

import java.util.List;

@Data
public class UserQuestionResultVO {
    // 是否通过标识
    private Integer pass; // 0  未通过  1 通过

    private String exeMessage; //异常信息

    private List<UserExeResult> userExeResultList; // 测试用例的输出

    @JsonIgnore
    private Integer score; // 得分
}
