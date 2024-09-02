truncate table category;

insert into `category` (`id`, `category_name`, `status`, `deleted`)
values ('1', '后端', '1', '0'),
       ('2', '前端', '1', '0'),
       ('3', '数据库', '1', '0'),
       ('4', '人工智能', '1', '0'),
       ('5', '开发工具', '1', '0'),
       ('6', '代码人生', '1', '0'),
       ('7', '阅读', '1', '0'),
       ('8', '操作系统', '1', '0'),
       ('9', '计算机网络', '1', '0');

insert into `tag` (`id`, `tag_name`, `tag_type`, `category_id`, `status`, `deleted`)
values ('137', '内存管理', '1', '8', '1', '0'),
       ('138', '磁盘', '1', '8', '1', '0'),
       ('139', '进程管理', '1', '8', '1', '0'),
       ('140', '线程', '1', '8', '1', '0'),
       ('141', 'TCP', '1', '9', '1', '0'),
       ('142', 'IP', '1', '9', '1', '0'),
       ('143', 'Http', '1', '9', '1', '0'),
       ('144', 'Netty', '1', '9', '1', '0');