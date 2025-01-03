package com.zmbdp.judge.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmbdp.common.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user_submit")
@EqualsAndHashCode(callSuper = true)
public class UserSubmit extends BaseEntity {

    @TableId(value = "SUBMIT_ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long submitId;

    private Long userId; // 唯一确定用户

    private Long questionId; // 唯一确定题目

    private Long examId; // 唯一确定竞赛

    private Integer programType;

    private String userCode;

    private Integer pass;

    private Integer score;

    private String exeMessage;

    private String caseJudgeRes; // 执行结果
}