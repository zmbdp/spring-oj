package com.zmbdp.friend.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zmbdp.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("tb_user_exam")
@EqualsAndHashCode(callSuper = true)
public class UserExam extends BaseEntity {

	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "USER_EXAM_ID", type = IdType.ASSIGN_ID)
	private Long userExamId; // 用户竞赛关系 id

	@JsonSerialize(using = ToStringSerializer.class)
	private Long examId; // 竞赛 id

	@JsonSerialize(using = ToStringSerializer.class)
	private Long userId; // 用户 id

	private Integer score; // 得分

	private Integer examRank; // 排名
}