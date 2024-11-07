package com.zmbdp.system.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 加密算法工具类，使用 BCrypt 算法实现
public class BCryptUtils {
    /**
     * 生成加密后的密码
     * @param password 传入的密码
     * @return 返回加密后的密文
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 比较两个密码是否相同
     * @param inputPassword 用户输入的密码
     * @param sqlPassword 数据库中查询到的密码
     * @return 比较的结果，相同为 true，不同为 false
     */
    public static boolean matchPassword(String inputPassword, String sqlPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(inputPassword, sqlPassword);
    }
}
