package com.zmbdp.friend.domain.user.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {

    private String headImage;

    private String nickName;

    private Integer sex;

    private String schoolName;

    private String majorName;

    private String phone;

    private String email;

    private String wechat;

    private String introduce;
}