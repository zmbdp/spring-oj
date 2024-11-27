package com.zmbdp.system.domain.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmbdp.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("tb_exam_question")
public class ExamQuestion extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long examQuestionId; // 竞赛题目关系 id

    private Long examId; // 题目 id

    private Long questionId; // 竞赛 id

    private Integer questionOrder; // 题目顺序
}

