package com.zmbdp.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.friend.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.friend.domain.exam.vo.ExamVO;
import com.zmbdp.friend.manager.ExamCacheManager;
import com.zmbdp.friend.mapper.exam.ExamMapper;
import com.zmbdp.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize()); // 设置页面也每页记录数
        return examMapper.selectExamList(examQueryDTO);
    }

    /**
     * 在 redis 中获取竞赛信息
     *
     * @param examQueryDTO 要求
     * @return 符合要求的数据
     */
    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        Long total = examCacheManager.getListSize(examQueryDTO.getType());
        List<ExamVO> examVOList;
        if (total == null || total <= 0) {
            // 说明从 redis 中未查询到数据，直接从数据库中查询就可以了
            examVOList = list(examQueryDTO);
            // 然后放到 redis 的缓存中
            examCacheManager.refreshCache(examQueryDTO.getType());
            // 从数据库中查到的数据直接调用 page 插件赋值
            total = new PageInfo<>(examVOList).getTotal();
        } else {
            // 如果有的话直接拿到这部分数据
            examVOList = examCacheManager.getExamVOList(examQueryDTO);
            // redis 中查数据的话直接就是 listSize，但是可能上一步出现问题，重新从数据库中刷新缓存了，这时候 size 就会改变
            total = examCacheManager.getListSize(examQueryDTO.getType());
        }
        if (CollectionUtil.isEmpty(examVOList)) {
            // 说明未查询到任何数据
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(examVOList, total);
    }
}
