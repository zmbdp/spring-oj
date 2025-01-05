package com.zmbdp.jop.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmbdp.jop.domain.user.UserSubmit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserSubmitMapper extends BaseMapper<UserSubmit> {
    List<Long> selectHostQuestionList();
}