package com.zmbdp.common.core.enums;

import lombok.Getter;

@Getter
public enum QuestionResult {

    ERROR(0), // 不通过

    PASS(1), // 通过
    ;

    private final Integer value;

    QuestionResult(Integer value) {
        this.value = value;
    }
}