package com.zmbdp.friend.domain.exam.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ExamRankVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId; // 用户 id

    private String nickName; //昵称

    private int examRank; // 排名

    private int score; // 得分

}
