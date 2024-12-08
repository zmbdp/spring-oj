package com.zmbdp.friend.domain.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zmbdp.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("tb_exam")
@EqualsAndHashCode(callSuper = true)
public class Exam extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long examId; // 竞赛 id

    private String title; // 竞赛标题

    private LocalDateTime startTime; // 竞赛开始时间

    private LocalDateTime endTime; // 竞赛结束时间

    private Integer status; // 是否发布 -> 0-未发布; 1-已发布
}
