CREATE TABLE `hot_project`
(
    `id`          int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`     varchar(128) NOT NULL DEFAULT '' COMMENT '项目名称',
    `description`       varchar(512) NOT NULL DEFAULT '' COMMENT '项目描述',
    `url`       varchar(512) NOT NULL DEFAULT '' COMMENT '项目跳转地址',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门项目表';

insert into hot_project (name, description, url)
values ('Spring Boot最佳入门学习项目', '手把手教你如何使用SpringBoot', 'https://github.com/mofan212/springboot-study');

insert into hot_project (name, description, url)
values ('async-profiler:CPU、堆栈监控', 'Sampling CPU and HEAP profiler for Java featuring AsyncGetCallTrace + perf_events', 'https://github.com/async-profiler/async-profiler');

insert into hot_project (name, description, url)
values ('电商秒杀系统', 'mall项目是一套电商系统，包括前台商城系统及后台管理系统', 'https://github.com/macrozheng/mall');

insert into hot_project (name, description, url)
values ('torch-MERF', 'google研究院开源的一款实时场景重建模型-Pytorch实现版', 'https://github.com/ashawkey/torch-merf');