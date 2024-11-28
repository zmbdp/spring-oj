package com.zmbdp.system.domain.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmbdp.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("tb_question")
@EqualsAndHashCode(callSuper = true)
public class Question extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long questionId; // 题目 id
    private String title; // 标题
    private Integer difficulty; // 难度
    private Long timeLimit; // 时间限制
    private Long spaceLimit; // 空间限制
    private String content; // 题目内容
    private String questionCase; // 题目用例
    private String defaultCode; // 默认代码块
    private String mainFuc; // main 函数
}
