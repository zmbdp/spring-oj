package com.zmbdp.jop.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.jop.domain.user.UserExam;
import com.zmbdp.jop.domain.user.UserScore;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserExamMapper extends BaseMapper<UserExam> {

    void updateUserScoreAndRank(List<UserScore> userScoreList);
}
