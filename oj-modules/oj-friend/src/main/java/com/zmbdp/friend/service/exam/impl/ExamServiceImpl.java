package com.zmbdp.friend.service.exam.impl;

import com.github.pagehelper.PageHelper;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.friend.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.friend.manager.ExamCacheManager;
import com.zmbdp.friend.mapper.exam.ExamMapper;
import com.zmbdp.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamServiceImpl extends BaseService implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamCacheManager examCacheManager;

    /**
     * 获取题目列表 service 层
     *
     * @param examQueryDTO 符合这些要求的题目
     * @return 这一页的数据
     */
    @Override
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize()); // 设置页面也每页记录数
        return getTableDataInfo(examMapper.selectExamList(examQueryDTO));
    }

    /**
     * 在 redis 中获取竞赛信息
     *
     * @param examQueryDTO 要求
     * @return 符合要求的数据
     */
    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        Long listSize = examCacheManager.getListSize(examQueryDTO.getType());
        return null;
    }
}
