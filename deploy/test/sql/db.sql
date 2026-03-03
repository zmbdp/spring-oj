use
spring_oj_nacos_local;

CREATE TABLE `config_info`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`            varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`           varchar(128)          DEFAULT NULL COMMENT 'group_id',
    `content`            longtext     NOT NULL COMMENT 'content',
    `md5`                varchar(32)           DEFAULT NULL COMMENT 'md5',
    `gmt_create`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`           text COMMENT 'source user',
    `src_ip`             varchar(50)           DEFAULT NULL COMMENT 'source ip',
    `app_name`           varchar(128)          DEFAULT NULL COMMENT 'app_name',
    `tenant_id`          varchar(128)          DEFAULT '' COMMENT '租户字段',
    `c_desc`             varchar(256)          DEFAULT NULL COMMENT 'configuration description',
    `c_use`              varchar(64)           DEFAULT NULL COMMENT 'configuration usage',
    `effect`             varchar(64)           DEFAULT NULL COMMENT '配置生效的描述',
    `type`               varchar(64)           DEFAULT NULL COMMENT '配置的类型',
    `c_schema`           text COMMENT '配置的模式',
    `encrypted_data_key` text         NOT NULL COMMENT '密钥',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

CREATE TABLE `config_info_aggr`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`      varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`     varchar(128) NOT NULL COMMENT 'group_id',
    `datum_id`     varchar(255) NOT NULL COMMENT 'datum_id',
    `content`      longtext     NOT NULL COMMENT '内容',
    `gmt_modified` datetime     NOT NULL COMMENT '修改时间',
    `app_name`     varchar(128) DEFAULT NULL COMMENT 'app_name',
    `tenant_id`    varchar(128) DEFAULT '' COMMENT '租户字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';

CREATE TABLE `config_info_beta`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`            varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`           varchar(128) NOT NULL COMMENT 'group_id',
    `app_name`           varchar(128)          DEFAULT NULL COMMENT 'app_name',
    `content`            longtext     NOT NULL COMMENT 'content',
    `beta_ips`           varchar(1024)         DEFAULT NULL COMMENT 'betaIps',
    `md5`                varchar(32)           DEFAULT NULL COMMENT 'md5',
    `gmt_create`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`           text COMMENT 'source user',
    `src_ip`             varchar(50)           DEFAULT NULL COMMENT 'source ip',
    `tenant_id`          varchar(128)          DEFAULT '' COMMENT '租户字段',
    `encrypted_data_key` text         NOT NULL COMMENT '密钥',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

CREATE TABLE `config_info_tag`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`      varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`     varchar(128) NOT NULL COMMENT 'group_id',
    `tenant_id`    varchar(128)          DEFAULT '' COMMENT 'tenant_id',
    `tag_id`       varchar(128) NOT NULL COMMENT 'tag_id',
    `app_name`     varchar(128)          DEFAULT NULL COMMENT 'app_name',
    `content`      longtext     NOT NULL COMMENT 'content',
    `md5`          varchar(32)           DEFAULT NULL COMMENT 'md5',
    `gmt_create`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`     text COMMENT 'source user',
    `src_ip`       varchar(50)           DEFAULT NULL COMMENT 'source ip',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

CREATE TABLE `config_tags_relation`
(
    `id`        bigint(20) NOT NULL COMMENT 'id',
    `tag_name`  varchar(128) NOT NULL COMMENT 'tag_name',
    `tag_type`  varchar(64)  DEFAULT NULL COMMENT 'tag_type',
    `data_id`   varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`  varchar(128) NOT NULL COMMENT 'group_id',
    `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
    `nid`       bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增长标识',
    PRIMARY KEY (`nid`),
    UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
    KEY         `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

CREATE TABLE `group_capacity`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id`          varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
    `quota`             int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
    `usage`             int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
    `max_size`          int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
    `max_aggr_count`    int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，0表示使用默认值',
    `max_aggr_size`     int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
    `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
    `gmt_create`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

CREATE TABLE `his_config_info`
(
    `id`                 bigint(20) unsigned NOT NULL COMMENT 'id',
    `nid`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增标识',
    `data_id`            varchar(255) NOT NULL COMMENT 'data_id',
    `group_id`           varchar(128) NOT NULL COMMENT 'group_id',
    `app_name`           varchar(128)          DEFAULT NULL COMMENT 'app_name',
    `content`            longtext     NOT NULL COMMENT 'content',
    `md5`                varchar(32)           DEFAULT NULL COMMENT 'md5',
    `gmt_create`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`           text COMMENT 'source user',
    `src_ip`             varchar(50)           DEFAULT NULL COMMENT 'source ip',
    `op_type`            char(10)              DEFAULT NULL COMMENT 'operation type',
    `tenant_id`          varchar(128)          DEFAULT '' COMMENT '租户字段',
    `encrypted_data_key` text         NOT NULL COMMENT '密钥',
    PRIMARY KEY (`nid`),
    KEY                  `idx_gmt_create` (`gmt_create`),
    KEY                  `idx_gmt_modified` (`gmt_modified`),
    KEY                  `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';

CREATE TABLE `tenant_capacity`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`         varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
    `quota`             int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
    `usage`             int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
    `max_size`          int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
    `max_aggr_count`    int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
    `max_aggr_size`     int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
    `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
    `gmt_create`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';

CREATE TABLE `tenant_info`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `kp`            varchar(128) NOT NULL COMMENT 'kp',
    `tenant_id`     varchar(128) DEFAULT '' COMMENT 'tenant_id',
    `tenant_name`   varchar(128) DEFAULT '' COMMENT 'tenant_name',
    `tenant_desc`   varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
    `create_source` varchar(32)  DEFAULT NULL COMMENT 'create_source',
    `gmt_create`    bigint(20) NOT NULL COMMENT '创建时间',
    `gmt_modified`  bigint(20) NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
    KEY             `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users`
(
    `username` varchar(50)  NOT NULL PRIMARY KEY COMMENT 'username',
    `password` varchar(500) NOT NULL COMMENT 'password',
    `enabled`  boolean      NOT NULL COMMENT 'enabled'
);

CREATE TABLE `roles`
(
    `username` varchar(50) NOT NULL COMMENT 'username',
    `role`     varchar(50) NOT NULL COMMENT 'role',
    UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions`
(
    `role`     varchar(50)  NOT NULL COMMENT 'role',
    `resource` varchar(128) NOT NULL COMMENT 'resource',
    `action`   varchar(8)   NOT NULL COMMENT 'action',
    UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

INSERT INTO users (username, password, enabled)
VALUES ('nacos', '$2a$10$7qvUS9QkQyMPRNEkeR1Wae60qOIPTNVeCMi5WPYGFL4EvdTRc6shu', TRUE);

INSERT INTO roles (username, role)
VALUES ('nacos', 'ROLE_ADMIN');

use
`xxl_job`;

SET NAMES utf8mb4;

CREATE TABLE `xxl_job_info`
(
    `id`                        int(11) NOT NULL AUTO_INCREMENT,
    `job_group`                 int(11) NOT NULL COMMENT '执行器主键ID',
    `job_desc`                  varchar(255) NOT NULL,
    `add_time`                  datetime              DEFAULT NULL,
    `update_time`               datetime              DEFAULT NULL,
    `author`                    varchar(64)           DEFAULT NULL COMMENT '作者',
    `alarm_email`               varchar(255)          DEFAULT NULL COMMENT '报警邮件',
    `schedule_type`             varchar(50)  NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
    `schedule_conf`             varchar(128)          DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
    `misfire_strategy`          varchar(50)  NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
    `executor_route_strategy`   varchar(50)           DEFAULT NULL COMMENT '执行器路由策略',
    `executor_handler`          varchar(255)          DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512)          DEFAULT NULL COMMENT '执行器任务参数',
    `executor_block_strategy`   varchar(50)           DEFAULT NULL COMMENT '阻塞处理策略',
    `executor_timeout`          int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
    `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    `glue_type`                 varchar(50)  NOT NULL COMMENT 'GLUE类型',
    `glue_source`               mediumtext COMMENT 'GLUE源代码',
    `glue_remark`               varchar(128)          DEFAULT NULL COMMENT 'GLUE备注',
    `glue_updatetime`           datetime              DEFAULT NULL COMMENT 'GLUE更新时间',
    `child_jobid`               varchar(255)          DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    `trigger_status`            tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
    `trigger_last_time`         bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
    `trigger_next_time`         bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_log`
(
    `id`                        bigint(20) NOT NULL AUTO_INCREMENT,
    `job_group`                 int(11) NOT NULL COMMENT '执行器主键ID',
    `job_id`                    int(11) NOT NULL COMMENT '任务，主键ID',
    `executor_address`          varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    `executor_handler`          varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
    `executor_sharding_param`   varchar(20)  DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    `trigger_time`              datetime     DEFAULT NULL COMMENT '调度-时间',
    `trigger_code`              int(11) NOT NULL COMMENT '调度-结果',
    `trigger_msg`               text COMMENT '调度-日志',
    `handle_time`               datetime     DEFAULT NULL COMMENT '执行-时间',
    `handle_code`               int(11) NOT NULL COMMENT '执行-状态',
    `handle_msg`                text COMMENT '执行-日志',
    `alarm_status`              tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
    PRIMARY KEY (`id`),
    KEY                         `I_trigger_time` (`trigger_time`),
    KEY                         `I_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_log_report`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `trigger_day`   datetime DEFAULT NULL COMMENT '调度-时间',
    `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
    `suc_count`     int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
    `fail_count`    int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
    `update_time`   datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_logglue`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `job_id`      int(11) NOT NULL COMMENT '任务，主键ID',
    `glue_type`   varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
    `glue_source` mediumtext COMMENT 'GLUE源代码',
    `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
    `add_time`    datetime    DEFAULT NULL,
    `update_time` datetime    DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_registry`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT,
    `registry_group` varchar(50)  NOT NULL,
    `registry_key`   varchar(255) NOT NULL,
    `registry_value` varchar(255) NOT NULL,
    `update_time`    datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY              `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_group`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `app_name`     varchar(64) NOT NULL COMMENT '执行器AppName',
    `title`        varchar(12) NOT NULL COMMENT '执行器名称',
    `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
    `address_list` text COMMENT '执行器地址列表，多地址逗号分隔',
    `update_time`  datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_user`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `username`   varchar(50) NOT NULL COMMENT '账号',
    `password`   varchar(50) NOT NULL COMMENT '密码',
    `role`       tinyint(4) NOT NULL COMMENT '角色：0-普通用户、1-管理员',
    `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `xxl_job_lock`
(
    `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
    PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化数据
INSERT INTO `xxl_job_group`(`id`, `app_name`, `title`, `address_type`, `address_list`, `update_time`)
VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31');

INSERT INTO `xxl_job_info`(`id`, `job_group`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`,
                           `schedule_type`, `schedule_conf`, `misfire_strategy`, `executor_route_strategy`,
                           `executor_handler`,
                           `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`,
                           `glue_type`,
                           `glue_source`, `glue_remark`, `glue_updatetime`, `child_jobid`)
VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        '2018-11-03 22:21:31', '');

INSERT INTO `xxl_job_user`(`id`, `username`, `password`, `role`, `permission`)
VALUES (1, 'admin', '374e9f392a65dbc1c48eb9bcbf90a9ee', 1, NULL);

INSERT INTO `xxl_job_lock` (`lock_name`)
VALUES ('schedule_lock');

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

insert into spring_oj_dev.tb_sys_user (user_id, user_account, nick_name, password, create_by, create_time, update_by,
                                       update_time)
values (522399972612771842, 'zhangsan', '超级管理员', '$2a$10$Nqf804r/D6jyF8A2rUlqZOsgEHcarQ1v/tX6OQIu3QSBnDVBAtb4u', 0,
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
    title         varchar(50)    not null comment '题目标题',
    difficulty    tinyint        not null comment '题目难度 1: 简单; 2: 普通 ; 3: 困难',
    time_limit    int comment '时间限制(ms)',
    space_limit   int comment '空间限制(Byte)',
    content       varchar(10240) not null comment '题目内容',
    question_case varchar(1024)  not null comment '题目用例',
    default_code  varchar(512)   not null comment '默认代码块',
    main_fuc      varchar(512)   not null comment 'main函数',
    create_by     bigint unsigned not null comment '创建人',
    create_time   datetime       not null comment '创建时间',
    update_by     bigint unsigned comment '更新人',
    update_time   datetime comment '更新时间',
    primary key (`question_id`)
) engine = innodb default character set = utf8mb4 comment = '题库的管理表';
INSERT INTO spring_oj_dev.tb_question (question_id, title, difficulty, time_limit, space_limit, content, question_case,
                                       default_code, main_fuc, create_by, create_time, update_by, update_time)
VALUES (2028720692609081346, '两数之和', 1, 1000000, 100000000, '<p>两个数字相加的和</p>',
        '[{"input":"1 2","output":"3"},{"input":"4 5","output":"9"}]', 'class Solution {
    public int twoSum(int var0, int var1) {

    }
}', '    public static void main(String[] var0) {
        Solution s = new Solution();
        int a = Integer.parseInt(var0[0]);
        int b = Integer.parseInt(var0[1]);
        System.out.println((s.twoSum(a, b)));
    }', 522399972612771842, '2026-03-03 14:34:40', 522399972612771842, '2026-03-03 14:40:24'),
       (2028722368430661634, '两数之差', 1, 1000000, 100000000, '<p>计算两个整数的差（第一个减第二个）</p>',
        '[{"input":"5 3","output":"2"},{"input":"10 20","output":"-10"}]', 'class Solution {
    public int subtract(int var0, int var1) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int a = Integer.parseInt(var0[0]);
    int b = Integer.parseInt(var0[1]);
    System.out.println(s.subtract(a, b));
}', 522399972612771842, '2026-03-03 14:41:19', 522399972612771842, '2026-03-03 14:41:19'),
       (2028726908487917570, '两数相乘', 1, 1000000, 100000000, '<p>计算两个整数的乘积</p>',
        '[{"input":"3 4","output":"12"},{"input":"-2 6","output":"-12"}]', 'class Solution {
    public int multiply(int var0, int var1) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int a = Integer.parseInt(var0[0]);
    int b = Integer.parseInt(var0[1]);
    System.out.println(s.multiply(a, b));
}', 522399972612771842, '2026-03-03 14:59:22', 522399972612771842, '2026-03-03 14:59:22'),
       (2028727096531148801, '两数最大值', 1, 1000000, 100000000, '<p>返回两个整数中的较大值</p>',
        '[{"input":"5 9","output":"9"},{"input":"10 3","output":"10"}]', 'class Solution {
    public int max(int var0, int var1) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int a = Integer.parseInt(var0[0]);
    int b = Integer.parseInt(var0[1]);
    System.out.println(s.max(a, b));
}', 522399972612771842, '2026-03-03 15:00:06', 522399972612771842, '2026-03-03 15:00:06'),
       (2028727472240123905, '判断奇偶', 1, 1000000, 100000000, '<p>判断第一个整数是否为偶数，是则返回1，否则返回0</p>',
        '[{"input":"4","output":"1"},{"input":"5","output":"0"}]', 'class Solution {
    public int isEven(int var0) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int a = Integer.parseInt(var0[0]);
    System.out.println(s.isEven(a));
}', 522399972612771842, '2026-03-03 15:01:36', 522399972612771842, '2026-03-03 15:01:36'),
       (2028727656340709377, '计算幂', 1, 1000000, 100000000, '<p>计算 var0 的 var1 次方（不考虑溢出）</p>',
        '[{"input":"2 3","output":"8"},{"input":"5 0","output":"1"}]', 'class Solution {
    public int power(int var0, int var1) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int a = Integer.parseInt(var0[0]);
    int b = Integer.parseInt(var0[1]);
    System.out.println(s.power(a, b));
}', 522399972612771842, '2026-03-03 15:02:20', 522399972612771842, '2026-03-03 15:02:20');

INSERT INTO spring_oj_dev.tb_question
(question_id, title, difficulty, time_limit, space_limit, content, question_case, default_code, main_fuc, create_by,
 create_time, update_by, update_time)
VALUES
-- 简单题 1
(2028731000000000001, '判断闰年', 1, 1000000, 100000000, '<p>判断一个年份是否是闰年，是返回1，否则返回0</p>',
 '[{"input":"2020","output":"1"},{"input":"1900","output":"0"}]', 'class Solution {
    public int isLeapYear(int year) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int year = Integer.parseInt(var0[0]);
    System.out.println(s.isLeapYear(year));
}', 522399972612771842, '2026-03-03 16:40:00', 522399972612771842, '2026-03-03 16:40:00'),

-- 普通题 2
(2028731000000000002, '数组求和', 2, 1000000, 100000000, '<p>计算一个整数数组的元素和</p>',
 '[{"input":"1 2 3 4","output":"10"},{"input":"-1 0 1","output":"0"}]', 'class Solution {
    public int arraySum(int[] nums) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int[] nums = Arrays.stream(var0).mapToInt(Integer::parseInt).toArray();
    System.out.println(s.arraySum(nums));
}', 522399972612771842, '2026-03-03 16:45:00', 522399972612771842, '2026-03-03 16:45:00'),

-- 普通题 3
(2028731000000000003, '字符串反转', 2, 1000000, 100000000, '<p>将输入字符串反转输出</p>',
 '[{"input":"hello","output":"olleh"},{"input":"abc","output":"cba"}]', 'class Solution {
    public String reverseString(String s) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    System.out.println(s.reverseString(var0[0]));
}', 522399972612771842, '2026-03-03 16:50:00', 522399972612771842, '2026-03-03 16:50:00'),

-- 困难题 4
(2028731000000000004, '两数之和（有序数组）', 3, 1000000, 100000000,
 '<p>给定一个升序数组和目标值，返回数组中两数之和等于目标值的索引</p>',
 '[{"input":"1 2 4 6 7 9 11 15\n10","output":"1 3"}]', 'class Solution {
    public int[] twoSumSorted(int[] nums, int target) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int target = Integer.parseInt(var0[var0.length-1]);
    int[] nums = Arrays.stream(Arrays.copyOf(var0,var0.length-1)).mapToInt(Integer::parseInt).toArray();
    int[] res = s.twoSumSorted(nums, target);
    System.out.println(Arrays.toString(res));
}', 522399972612771842, '2026-03-03 16:55:00', 522399972612771842, '2026-03-03 16:55:00'),

-- 简单题 5
(2028731000000000005, '判断回文数', 1, 1000000, 100000000, '<p>判断一个整数是否是回文数，是返回1，否则返回0</p>',
 '[{"input":"121","output":"1"},{"input":"123","output":"0"}]', 'class Solution {
    public int isPalindrome(int x) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int x = Integer.parseInt(var0[0]);
    System.out.println(s.isPalindrome(x));
}', 522399972612771842, '2026-03-03 17:00:00', 522399972612771842, '2026-03-03 17:00:00'),

-- 普通题 6
(2028731000000000006, '最大子数组和', 2, 1000000, 100000000, '<p>计算数组中连续子数组的最大和</p>',
 '[{"input":"-2 1 -3 4 -1 2 1 -5 4","output":"6"}]', 'class Solution {
    public int maxSubArray(int[] nums) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int[] nums = Arrays.stream(var0).mapToInt(Integer::parseInt).toArray();
    System.out.println(s.maxSubArray(nums));
}', 522399972612771842, '2026-03-03 17:05:00', 522399972612771842, '2026-03-03 17:05:00'),

-- 困难题 7
(2028731000000000007, '快速幂', 3, 1000000, 100000000,
 '<p>计算 var0 的 var1 次方，使用快速幂算法（考虑大数溢出可用 long）</p>',
 '[{"input":"2 10","output":"1024"},{"input":"3 5","output":"243"}]', 'class Solution {
    public long fastPower(int var0,int var1) {

    }
}', 'public static void main(String[] var0) {
    Solution s = new Solution();
    int a = Integer.parseInt(var0[0]);
    int b = Integer.parseInt(var0[1]);
    System.out.println(s.fastPower(a,b));
}', 522399972612771842, '2026-03-03 17:10:00', 522399972612771842, '2026-03-03 17:10:00');

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
    submit_id      bigint unsigned not null comment '提交记录id',
    user_id        bigint unsigned not null comment '用户id',
    question_id    bigint unsigned not null comment '题目id',
    exam_id        bigint unsigned  comment '竞赛id',
    program_type   tinyint  not null comment '代码类型 0-java   1-cpp',
    user_code      text     not null comment '用户代码',
    pass           tinyint  not null comment '0：未通过  1：通过',
    exe_message    varchar(1024) comment '执行结果',
    case_judge_res varchar(2048) comment '测试用例输出结果',
    score          int      not null default '0' comment '得分',
    create_by      bigint unsigned not null  comment '创建人',
    create_time    datetime not null comment '创建时间',
    update_by      bigint unsigned  comment '更新人',
    update_time    datetime comment '更新时间',
    primary key (`submit_id`)
) engine = innodb default character set = utf8mb4 comment = '用户提交表';