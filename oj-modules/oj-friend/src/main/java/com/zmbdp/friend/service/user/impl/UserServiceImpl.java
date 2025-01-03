package com.zmbdp.friend.service.user.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.zmbdp.common.core.constants.CacheConstants;
import com.zmbdp.common.core.constants.Constants;
import com.zmbdp.common.core.constants.HttpConstants;
import com.zmbdp.common.core.constants.UserConstants;
import com.zmbdp.common.core.domain.LoginUser;
import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.core.domain.vo.LoginUserVO;
import com.zmbdp.common.core.enums.ResultCode;
import com.zmbdp.common.core.enums.UserIdentity;
import com.zmbdp.common.core.enums.UserStatus;
import com.zmbdp.common.core.service.BaseService;
import com.zmbdp.common.core.utils.ThreadLocalUtil;
import com.zmbdp.common.message.service.AliSmsService;
import com.zmbdp.common.redis.service.RedisService;
import com.zmbdp.common.security.exception.ServiceException;
import com.zmbdp.common.security.service.TokenService;
import com.zmbdp.friend.domain.user.User;
import com.zmbdp.friend.domain.user.dto.UserDTO;
import com.zmbdp.friend.domain.user.dto.UserUpdateDTO;
import com.zmbdp.friend.domain.user.vo.UserVO;
import com.zmbdp.friend.manager.UserCacheManager;
import com.zmbdp.friend.mapper.user.UserMapper;
import com.zmbdp.friend.service.user.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RefreshScope
public class UserServiceImpl extends BaseService implements IUserService {

    @Autowired
    private AliSmsService aliSmsService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserCacheManager userCacheManager;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;

    @Value("${sms.code-limit:3}")
    private Integer sendLimit;

    // 是否开启短信验证的验证的开关 -> true: 开启，false: 关闭
    @Value("${sms.is-send:false}")
    private Boolean isSend;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${file.oss.downloadUrl}")
    private String downloadUrl;

    /**
     * 判断是不是正常的手机号
     *
     * @param phone 用户的手机号
     * @return 是否是正常的
     */
    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }

    /**
     * C端用户登录获取验证码 service 层
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
        // 根据 key 拿到剩余的时间
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
        String code = isSend ? RandomUtil.randomNumbers(6) : Constants.DEFAULT_CODE;
        // 如果这个开关是打开的，那么直接发送验证码到手机上，如果是关闭的，那么直接存 123456 就行了
        if (isSend) {
            // 然后再开始发送
            boolean result = aliSmsService.sendMobileCode(userDTO.getPhone(), code);
            if (!result) {
                return Result.fail(ResultCode.FAILED_SEND_CODE);
            }
        }
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

    /**
     * 登录逻辑的 service
     *
     * @param phone 用户手机号
     * @param code  用户验证码
     * @return token
     */
    @Override
    public Result<String> codeLogin(String phone, String code) {
        // 先比比验证码
        Result<String> isCodeValid = checkCode(phone, code);
        if (isCodeValid != null) {
            return isCodeValid;
        }
        // 然后再判断用户身份
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        // 判断是不是新用户
        if (user == null) {
            // 说明是新用户，因为已经判断验证码是否正确了，所以直接添加到数据库中即可
            user = new User();
            user.setCreateBy(Constants.SYSTEM_USER_ID);
            user.setPhone(phone);
            user.setNickName(UserConstants.DEFAULT_NICK_NAME);
            user.setStatus(UserStatus.Normal.getValue());
            user.setHeadImage(UserConstants.DEFAULT_HEAD_IMAGE);
            userMapper.insert(user);
        }

        // 说明是老用户，直接登录就行了
        // 生成一个 token，返回给前端
        String token = tokenService.createToken(user.getUserId(), user.getNickName(),
                secret, UserIdentity.ORDINARY.getValue(), user.getHeadImage());
        return Result.success(token);
    }

    /**
     * C端用户退出登录功能代码
     *
     * @param token 令牌
     * @return 是否成功
     */
    @Override
    public Result<Void> logout(String token) {
        if (StringUtils.isEmpty(token)) {
            return Result.fail(ResultCode.ERROR);
        }
        // 从数据库中删除这个信息
        return toResult(tokenService.delLoginUser(token, secret));
    }

    @Override
    public Result<LoginUserVO> info(String token) {
        if (StringUtils.isEmpty(token)) {
            return Result.fail(ResultCode.ERROR);
        }
        if (StringUtils.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StringUtils.EMPTY);
        }
        LoginUser loginUser = tokenService.getLoginUser(token, secret);
        if (loginUser == null) {
            return Result.fail();
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        if (StringUtils.isNotEmpty(loginUser.getHeadImage())) {
            loginUserVO.setHeadImage(downloadUrl + loginUser.getHeadImage());
        }
        return Result.success(loginUserVO);
    }

    /**
     * 获取用户信息的 service 层
     *
     * @return 用户的信息
     */
    @Override
    public Result<UserVO> detail() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            return Result.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        // 从缓存当中拿到用户数据（在这里如果缓存中拿不到的话会操作数据库）
        UserVO userVO = userCacheManager.getUserById(userId);
        if (userVO == null) {
            // 说明缓存和数据库都没有，就是没这个用户了
            return Result.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        // 看看头像是否为空，如果不为空就是设置了头像，直接拼接
        if (StrUtil.isNotEmpty(userVO.getHeadImage())) {
            userVO.setHeadImage(downloadUrl + userVO.getHeadImage());
        }
        return Result.success(userVO);
    }

    /**
     * 编辑用户数据的 service 层
     *
     * @param userUpdateDTO 修改好的用户数据
     * @return 是否修改成功
     */
    @Override
    public Result<Void> edit(UserUpdateDTO userUpdateDTO) {
        return getVoidResult(userUpdateDTO, null);
    }

    /**
     * 更新头像 service 层
     *
     * @param headImage 更新的头像唯一标志
     * @return 是否更新完成
     */
    @Override
    public Result<Void> updateHeadImage(String headImage) {
        return getVoidResult(null, headImage);
    }

    /**
     * 更新用户数据
     *
     * @param userUpdateDTO 需要更新的用户数据
     * @param headImage 用户头像
     * @return 是否更新完成
     */
    private Result<Void> getVoidResult(UserUpdateDTO userUpdateDTO, String headImage) {
        // 先拿到用户 id
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        if (userId == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        // 再从数据库里面查询是否有这个用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (headImage != null) {
            // 不是空就更新头像
            user.setHeadImage(headImage);
        } else {
            // 是空就更新用户数据，不能更新头像，因为返回过来的是完整的
            user.setNickName(userUpdateDTO.getNickName());
            user.setSex(userUpdateDTO.getSex());
            user.setSchoolName(userUpdateDTO.getSchoolName());
            user.setMajorName(userUpdateDTO.getMajorName());
            user.setPhone(userUpdateDTO.getPhone());
            user.setEmail(userUpdateDTO.getEmail());
            user.setWechat(userUpdateDTO.getWechat());
            user.setIntroduce(userUpdateDTO.getIntroduce());
        }
        // 更新用户缓存
        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),
                ThreadLocalUtil.get(Constants.USER_KEY, String.class));
        // 更新数据库的数据
        return toResult(userMapper.updateById(user));
    }

    @Nullable
    private Result<String> checkCode(String phone, String code) {
        // 获取到 redis 中的 key，看看他验证码是什么
        String phoneKey = getPhoneKey(phone);
        String cacheCode = redisService.getCacheObject(phoneKey, String.class);
        if (StrUtil.isEmpty(cacheCode)) {
            // 如果是空的，说明失效了，让用户重新获取一遍
            return Result.fail(ResultCode.FAILED_INVALID_CODE);
        }
        // 如果不是空的，那就比较两个验证码是否相同
        if (!cacheCode.equals(code)) {
            // 不相同就说验证码错误
            return Result.fail(ResultCode.FAILED_ERROR_CODE);
        }
        // 走到这里说明验证码比对成功了，然后删除掉 redis 中的这个验证码
        redisService.deleteObject(phoneKey);
        return null;
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
