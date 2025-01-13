package com.zmbdp.friend.domain.exam.vo;

import lombok.Data;

@Data
public class ExamRankVO {

    private Long userId; // 用户 id

    private String nickName; //昵称

    private int examRank; // 排名

    private int score; // 得分

}
