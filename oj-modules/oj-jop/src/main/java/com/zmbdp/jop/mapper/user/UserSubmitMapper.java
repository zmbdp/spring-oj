package com.zmbdp.jop.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.jop.domain.user.UserScore;
import com.zmbdp.jop.domain.user.UserSubmit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserSubmitMapper extends BaseMapper<UserSubmit> {
    List<UserScore> selectUserScoreList(Set<Long> examIdSet);

    List<Long> selectHostQuestionList();
}