package com.zmbdp.common.core.service;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.enums.ResultCode;

public class BaseService {

    public Result<Void> toResult(int rows) {
        return rows > 0 ? Result.success() : Result.fail(ResultCode.ERROR);
    }

    public Result<Void> toResult(boolean result) {
        return result ? Result.success() : Result.fail(ResultCode.ERROR);
    }
}
