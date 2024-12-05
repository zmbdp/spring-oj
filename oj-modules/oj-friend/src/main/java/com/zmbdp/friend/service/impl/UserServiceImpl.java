package com.zmbdp.friend.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.util.StringUtil;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.message.service.AliSmsService;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.friend.domain.dto.UserDTO;
import com.zmbdp.friend.mapper.UserMapper;
import com.zmbdp.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl extends BaseService implements IUserService {

    @Autowired
    private AliSmsService aliSmsService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;

    @Value("${sms.code-limit:3}")
    private Integer sendLimit;

    /**
     * C端用户获取验证码请求
     *
     * @param userDTO 登录参数
     * @return 成功与否
     */
    @Override
    public Result<Void> sendCode(UserDTO userDTO) {
        // 判断是不是正确的手机号
        if (!checkPhone(userDTO.getPhone())) {
            return Result.fail(ResultCode.FAILED_USER_PHONE);
        }

        // 正确了继续往下走
        // 先判断用户是否频繁操作
        String phoneKet = getPhoneKey(userDTO.getPhone());
        Long expire = redisService.getExpire(phoneKet, TimeUnit.SECONDS);
        // 如果说从 redis 中查到了，并且剩余时间大于 60s 的话，那就是频繁操作了
        if ((StringUtil.isNotEmpty(phoneKet)) && ((phoneCodeExpiration * 60) - expire < 60)) {
            return Result.fail(ResultCode.FAILED_FREQUENT);
        }

        // 还得限制每个用户每天只能获取 20 次验证码，第二天清零
        String codeKey = getCodeKey(userDTO.getPhone());
        // 拿出这个，看看获取了多少次验证码
        Long sendTimes = redisService.getCacheObject(codeKey, Long.class);
        if (sendTimes != null && sendTimes >= sendLimit) {
            return Result.fail(ResultCode.FAILED_TIME_LIMIT);
        }

        // 开始生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 然后再开始发送
        boolean result = aliSmsService.sendMobileCode(userDTO.getPhone(), code);
        if (result) {
            // 只有成功了才存储到 redis 中，设置过期时间，然后设置过期时间
            redisService.setCacheObject(phoneKet, code, phoneCodeExpiration, TimeUnit.MINUTES);
            // 发送成功之后 redis 记录的次数 key 再自增一下，
            // 如果说不存在，那么就会自动生成一个这个 value 为 1 的 key
            redisService.increment(codeKey);
            // 如果说当前次数没有的话，那么就说明是当天第一次获取验证码
            if (sendTimes == null) {
                // 利用算法动态计算好时间长度
                long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
                redisService.expire(codeKey, seconds, TimeUnit.SECONDS);
            }
            return Result.success();
        }
        return Result.fail(ResultCode.FAILED_SEND_CODE);
    }

    /**
     * 判断是不是正常的手机号
     *
     * @param phone 用户的手机号
     * @return 是否是正常的
     */
    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }

    /**
     * 生成 redis 中用户验证码的 key
     *
     * @param phone 用户手机号
     * @return 根据 phone 生成的 key
     */
    private String getPhoneKey(String phone) {
        return CacheConstants.PHONE_CODE_KEY + phone;
    }

    /**
     * 生成 redis 中 code 的 key
     *
     * @param phone 用户手机号
     * @return 根据 code 生成的 key
     */
    private String getCodeKey(String phone) {
        return CacheConstants.CODE_TIME_KEY + phone;
    }
}
