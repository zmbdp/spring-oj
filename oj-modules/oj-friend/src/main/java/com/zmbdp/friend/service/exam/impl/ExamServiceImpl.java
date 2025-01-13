package com.zmbdp.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.core.utils.ThreadLocalUtil;
import com.zmbdp.friend.domain.exam.dto.ExamQueryDTO;
import com.zmbdp.friend.domain.exam.dto.ExamRankDTO;
import com.zmbdp.friend.domain.exam.vo.ExamRankVO;
import com.zmbdp.friend.domain.exam.vo.ExamVO;
import com.zmbdp.friend.domain.user.vo.UserVO;
import com.zmbdp.friend.manager.ExamCacheManager;
import com.zmbdp.friend.manager.UserCacheManager;
import com.zmbdp.friend.mapper.exam.ExamMapper;
import com.zmbdp.friend.mapper.user.UserExamMapper;
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

    @Autowired
    private UserExamMapper userExamMapper;

    @Autowired
    private UserCacheManager userCacheManager;

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
        Long total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        List<ExamVO> examVOList;
        if (total == null || total <= 0) {
            // 说明从 redis 中未查询到数据，直接从数据库中查询就可以了
            examVOList = list(examQueryDTO);
            // 然后放到 redis 的缓存中
            examCacheManager.refreshCache(examQueryDTO.getType(), null);
            // 从数据库中查到的数据直接调用 page 插件赋值
            total = new PageInfo<>(examVOList).getTotal();
        } else {
            // 如果有的话直接拿到这部分数据
            examVOList = examCacheManager.getExamVOList(examQueryDTO, null);
            // redis 中查数据的话直接就是 listSize，但是可能上一步出现问题，重新从数据库中刷新缓存了，这时候 size 就会改变
            total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        }
        if (CollectionUtil.isEmpty(examVOList)) {
            // 说明未查询到任何数据
            return TableDataInfo.empty();
        }
        assembleExamVOList(examVOList);
        return TableDataInfo.success(examVOList, total);
    }

    /**
     * 获取竞赛中第一题的 service 层
     *
     * @param examId 竞赛 id
     * @return 第一题的题目 id
     */
    @Override
    public String getFirstQuestion(Long examId) {
        // 先判断缓存是否有数据
        checkAndRefresh(examId);
        // 然后直接返回缓存当中的数据
        return examCacheManager.getFirstQuestion(examId).toString();
    }

    /**
     * 获取上一题 id 的 service 层
     *
     * @param examId     竞赛 id
     * @param questionId 题目 id
     * @return 上一题的题目 id
     */
    @Override
    public String preQuestion(Long examId, Long questionId) {
        // 先判断缓存是否有数据
        checkAndRefresh(examId);
        // 然后直接返回缓存中的数据
        return examCacheManager.preQuestion(examId, questionId).toString();
    }

    /**
     * 获取下一题 id 的 service 层
     *
     * @param examId     竞赛 id
     * @param questionId 题目 id
     * @return 下一题的题目 id
     */
    @Override
    public String nextQuestion(Long examId, Long questionId) {
        // 先判断缓存是否有数据
        checkAndRefresh(examId);
        // 然后直接返回缓存中的数据
        return examCacheManager.nextQuestion(examId, questionId).toString();
    }

    /**
     * 获取竞赛排名的 service 层
     *
     * @param examRankDTO 竞赛排名需要的相关参数
     * @return 排名列表
     */
    @Override
    public TableDataInfo rankList(ExamRankDTO examRankDTO) {
        // 先查缓存，没有就查数据库
        Long total = examCacheManager.getRankListSize(examRankDTO.getExamId());
        List<ExamRankVO> examRankVOList;
        if (total == null || total <= 0) {
            // 没有就查数据库
            PageHelper.startPage(examRankDTO.getPageNum(), examRankDTO.getPageSize());
            examRankVOList = userExamMapper.selectExamRankList(examRankDTO.getExamId());
            examCacheManager.refreshExamRankCache(examRankDTO.getExamId());
            total = new PageInfo<>(examRankVOList).getTotal();
        } else {
            examRankVOList = examCacheManager.getExamRankList(examRankDTO);
        }
        if (CollectionUtil.isEmpty(examRankVOList)) {
            return TableDataInfo.empty();
        }
        assembleExamRankVOList(examRankVOList);
        return TableDataInfo.success(examRankVOList, total);
    }

    private void checkAndRefresh(Long examId) {
        // 先判断缓存是否有数据
        Long listSize = examCacheManager.getExamQuestionListSize(examId);
        if (listSize == null || listSize <= 0) {
            // 如果没有就刷新缓存
            examCacheManager.refreshExamQuestionCache(examId);
        }
    }
    private void assembleExamRankVOList(List<ExamRankVO> examRankVOList) {
        if (CollectionUtil.isEmpty(examRankVOList)) {
            return;
        }
        for (ExamRankVO examRankVO : examRankVOList) {
            Long userId = examRankVO.getUserId();
            UserVO user = userCacheManager.getUserById(userId);
            examRankVO.setNickName(user.getNickName());
        }
    }

    private void assembleExamVOList(List<ExamVO> examVOList) {
        // 从 ThreadLocal 中获取用户 id
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        List<Long> userExamIdList = examCacheManager.getAllUserExamList(userId);
        if (CollectionUtil.isEmpty(userExamIdList)) {
            return;
        }
        for (ExamVO examVO : examVOList) {
            if (userExamIdList.contains(examVO.getExamId())) {
                examVO.setEnter(true);
            }
        }
    }
}
