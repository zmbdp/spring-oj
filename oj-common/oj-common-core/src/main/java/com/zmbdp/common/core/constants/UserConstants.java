package com.zmbdp.common.core.constants;

import java.util.UUID;

public class UserConstants {
    public static final String DEFAULT_NICK_NAME = "LaatCode" + generateNickName();
    public static final String DEFAULT_INTRODUCE = "关于你的个性，兴趣或经验，展现自己独一档的强度...";
    // 生成16位的默认昵称
    public static String generateNickName() {
        // 生成一个UUID
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 截取前16位作为昵称
        return uuid.substring(0, 16);
    }
}
