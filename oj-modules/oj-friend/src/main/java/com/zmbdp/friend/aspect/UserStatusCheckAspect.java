package com.zmbdp.friend.aspect;

import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.utils.ThreadLocalUtil;
import com.zmbdp.common.security.exception.ServiceException;
import com.zmbdp.friend.domain.user.vo.UserVO;
import com.zmbdp.friend.manager.UserCacheManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class UserStatusCheckAspect {

    @Autowired
    private UserCacheManager userCacheManager;

    @Before(value = "@annotation(com.zmbdp.friend.aspect.CheckUserStatus)")
    public void before(JoinPoint point) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        UserVO user = userCacheManager.getUserById(userId);
        // 如果什么信息都没拿到，说明还没有这个用户
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        // 如果说是 0 的话，说明是拉黑用户，得限制一下了
        if (Objects.equals(user.getStatus(), Constants.FALSE)) {
            throw new ServiceException(ResultCode.FAILED_USER_BANNED);
        }
    }
}
