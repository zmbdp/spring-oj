package com.zmbdp.common.core.constants;

import java.util.UUID;

public class UserConstants {
    public static final String DEFAULT_NICK_NAME = "LaatCode_" + generateNickName();
    public static final String DEFAULT_HEAD_IMAGE = "default-head-image.jpg";
    // 生成16位的默认昵称
    public static String generateNickName() {
        // 生成一个UUID
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 截取前16位作为昵称
        return uuid.substring(0, 16);
    }
}
