package com.zmbdp.friend.domain.question.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionDetailVO extends QuestionVO {

    private Long timeLimit; // 时间限制

    private Long spaceLimit; // 空间限制

    private String content; // 内容

    private String defaultCode; // 默认代码块
}
