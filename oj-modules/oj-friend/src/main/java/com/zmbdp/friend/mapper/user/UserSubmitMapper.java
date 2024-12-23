package com.zmbdp.friend.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.friend.domain.user.UserSubmit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserSubmitMapper extends BaseMapper<UserSubmit> {

    UserSubmit selectCurrentUserSubmit(Long userId, Long examId, Long questionId, String currentTime);

    List<Long> selectHostQuestionList();
}