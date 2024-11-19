-- 要求：表名小写、多个单词用下划线隔开、全部都以 tb 开头
create database if not exists spring_oj_dev charset utf8mb4;

use spring_oj_dev;

-- 关于管理员的用户表
drop table if exists tb_sys_user;
create table tb_sys_user
(
    user_id      bigint unsigned not null comment '用户id（主键）',
    user_account varchar(128) not null comment '账号',
    nick_name    varchar(128) comment '用户昵称',
    password     varchar(128) not null comment '密码',
    create_by    bigint unsigned not null comment '创建人',
    create_time  datetime     not null comment '创建时间',
    update_by    bigint unsigned comment '更新人',
    update_time  datetime comment '更新时间',
    primary key (`user_id`),
    unique key `idx_user_account` (`user_account`)
) engine = innodb default character set = utf8mb4 comment = '管理员的用户表';

insert into spring_oj_dev.tb_sys_user (user_id, user_account, password, create_by, create_time, update_by, update_time)
values (0, 'zhangsan', '$2a$10$Nqf804r/D6jyF8A2rUlqZOsgEHcarQ1v/tX6OQIu3QSBnDVBAtb4u', 0, '2024-11-07 10:30:00', 0,
        '2024-11-07 10:30:00');