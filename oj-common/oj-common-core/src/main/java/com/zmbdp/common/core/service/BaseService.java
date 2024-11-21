package com.zmbdp.common.core.service;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.TableDataInfo;
import com.zmbdp.common.core.enums.ResultCode;

import java.util.List;

public class BaseService {

    public Result<Void> toResult(int rows) {
        return rows > 0 ? Result.success() : Result.fail(ResultCode.ERROR);
    }

    public Result<Void> toResult(boolean result) {
        return result ? Result.success() : Result.fail(ResultCode.ERROR);
    }

    public TableDataInfo getTableDataInfo(List<?> list) {
        if (CollectionUtil.isEmpty(list)) {
            // 说明未查询到任何数据
            return TableDataInfo.empty();
        }
//        new PageInfo(questionVOList).getTotal();// 获取符合条件的数据总数
        return TableDataInfo.success(list, new PageInfo<>(list).getTotal());
    }
}
