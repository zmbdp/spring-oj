create user 'zmbdp'@'%' identified by '123456';
create database if not exists `spring_oj_dev`;
create database if not exists `spring_oj_nacos_local`;
create database if not exists `xxl_job` default character set utf8mb4 collate utf8mb4_unicode_ci;

grant create, drop, select, insert, update, delete, alter on spring_oj_dev.* to 'zmbdp'@'%';
grant create, drop, select, insert, update, delete, alter on spring_oj_nacos_local.* to 'zmbdp'@'%';
grant create, drop, select, insert, update, delete, alter on xxl_job.* to 'zmbdp'@'%';
