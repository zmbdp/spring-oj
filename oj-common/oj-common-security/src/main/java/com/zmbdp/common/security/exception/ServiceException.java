package com.zmbdp.common.security.exception;

import com.zmbdp.common.core.enums.ResultCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private ResultCode resultCode;
    public ServiceException(ResultCode resultCode){
        this.resultCode = resultCode;
    }
}
