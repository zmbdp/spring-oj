package com.zmbdp.friend.domain.user.dto;

import lombok.Data;

@Data
public class UserSubmitDTO {

    private Long examId; //可选

    private Long questionId;

    private Integer programType; // (0: java  1:c/cpp)

    private String userCode;
}