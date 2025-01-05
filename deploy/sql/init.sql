-- 要求：表名小写、多个单词用下划线隔开、全部都以 tb 开头
create
database if not exists spring_oj_dev charset utf8mb4;

use
spring_oj_dev;

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
values (522399972612771842, 'zhangsan', '$2a$10$Nqf804r/D6jyF8A2rUlqZOsgEHcarQ1v/tX6OQIu3QSBnDVBAtb4u', 0,
        '2024-11-07 10:30:00', 0,
        '2024-11-07 10:30:00');

-- 关于题库的管理表
/* 测试用例演示，使用 json 来存
[
    {
        "input": "1 2",
        "output": "3"
    },
    {
        "input": "4 5",
        "output": "9"
    }
]
*/
drop table if exists tb_question;
create table tb_question
(
    question_id   bigint unsigned not null comment '题目id(主键)',
    title         varchar(50)   not null comment '题目标题',
    difficulty    tinyint       not null comment '题目难度 1: 简单; 2: 普通 ; 3: 困难',
    time_limit    int comment '时间限制(ms)',
    space_limit   int comment '空间限制(Byte)',
    content       varchar(1024) not null comment '题目内容',
    question_case varchar(1024) not null comment '题目用例',
    default_code  varchar(512)  not null comment '默认代码块',
    main_fuc      varchar(512)  not null comment 'main函数',
    create_by     bigint unsigned not null comment '创建人',
    create_time   datetime      not null comment '创建时间',
    update_by     bigint unsigned comment '更新人',
    update_time   datetime comment '更新时间',
    primary key (`question_id`)
) engine = innodb default character set = utf8mb4 comment = '题库的管理表';

-- 竞赛相关的
drop table if exists tb_exam;
create table tb_exam
(
    exam_id     bigint unsigned not null comment '竞赛id（主键）',
    title       varchar(50) not null comment '竞赛标题',
    start_time  datetime    not null comment '竞赛开始时间',
    end_time    datetime    not null comment '竞赛结束时间',
    status      tinyint     not null default '0' comment '是否发布 0：未发布 1：已发布',
    create_by   bigint unsigned not null comment '创建人',
    create_time datetime    not null comment '创建时间',
    update_by   bigint unsigned comment '更新人',
    update_time datetime comment '更新时间',
    primary key (exam_id)
) engine = innodb default character set = utf8mb4 comment = '竞赛表';

drop table if exists tb_exam_question;
create table tb_exam_question
(
    exam_question_id bigint unsigned not null comment '竞赛题目关系id（主键）',
    question_id      bigint unsigned not null comment '题目id',
    exam_id          bigint unsigned not null comment '竞赛id',
    question_order   int      not null comment '题目顺序',
    create_by        bigint unsigned not null comment '创建人',
    create_time      datetime not null comment '创建时间',
    update_by        bigint unsigned comment '更新人',
    update_time      datetime comment '更新时间',
    primary key (exam_question_id)
) engine = innodb default character set = utf8mb4 comment = '竞赛题目关系表';

-- 关于   对C端用户的管理的表
drop table if exists tb_user;
create table tb_user
(
    user_id     bigint unsigned not null comment '用户id（主键）',
    nick_name   varchar(64) comment '用户昵称',
    head_image  varchar(128) comment '用户头像',
    sex         tinyint comment '用户状态 1: 男  2：女',
    phone       char(11) not null comment '手机号',
    code        char(6) comment '验证码',
    email       varchar(64) comment '邮箱',
    wechat      varchar(64) comment '微信号',
    school_name varchar(64) comment '学校',
    major_name  varchar(64) comment '专业',
    introduce   varchar(128) comment '个人介绍',
    status      tinyint  not null default '1' comment '用户状态 0: 拉黑  1：正常',
    create_by   bigint unsigned not null  comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by   bigint unsigned  comment '更新人',
    update_time datetime comment '更新时间',
    primary key (`user_id`)
) engine = innodb default character set = utf8mb4 comment = 'C端用户管理表';


drop table if exists tb_user_exam;
create table tb_user_exam
(
    user_exam_id bigint unsigned not null comment '用户竞赛关系id',
    user_id      bigint unsigned not null comment '用户id',
    exam_id      bigint unsigned not null comment '竞赛id',
    score        int unsigned comment '得分',
    exam_rank    int unsigned comment '排名',
    create_by    bigint unsigned not null comment '创建人',
    create_time  datetime not null comment '创建时间',
    update_by    bigint unsigned comment '更新人',
    update_time  datetime comment '更新时间',
    primary key (user_exam_id)
) engine = innodb default character set = utf8mb4 comment = '用户竞赛关系表';

-- 关于用户消息的表
drop table if exists tb_message;
create table tb_message
(
    message_id  bigint unsigned not null comment '消息id（主键）',
    text_id     bigint unsigned not null comment '消息内容id（外键）',
    send_id     bigint unsigned not null comment '消息发送人id',
    rec_id      bigint unsigned not null comment '消息接收人id',
    create_by   bigint unsigned not null  comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by   bigint unsigned  comment '更新人',
    update_time datetime comment '更新时间',
    primary key (message_id)
) engine = innodb default character set = utf8mb4 comment = '消息表';

drop table if exists tb_message_text;
create table tb_message_text
(
    text_id         bigint unsigned not null comment '消息内容id（主键）',
    message_title   varchar(16)  not null comment '消息标题',
    message_content varchar(512) not null comment '消息内容',
    create_by       bigint unsigned not null  comment '创建人',
    create_time     datetime     not null comment '创建时间',
    update_by       bigint unsigned  comment '更新人',
    update_time     datetime comment '更新时间',
    primary key (text_id)
) engine = innodb default character set = utf8mb4 comment = '消息内容表';


drop table if exists tb_user_submit;
create table tb_user_submit
(
    submit_id    bigint unsigned not null comment '提交记录id',
    user_id      bigint unsigned not null comment '用户id',
    question_id  bigint unsigned not null comment '题目id',
    exam_id      bigint unsigned  comment '竞赛id',
    program_type tinyint  not null comment '代码类型 0-java   1-cpp',
    user_code    text     not null comment '用户代码',
    pass         tinyint  not null comment '0：未通过  1：通过',
    exe_message  varchar(1024) comment '执行结果',
    case_judge_res varchar(2048) comment '测试用例输出结果',
    score        int      not null default '0' comment '得分',
    create_by    bigint unsigned not null  comment '创建人',
    create_time  datetime not null comment '创建时间',
    update_by    bigint unsigned  comment '更新人',
    update_time  datetime comment '更新时间',
    primary key (`submit_id`)
) engine = innodb default character set = utf8mb4 comment = '用户提交表';









