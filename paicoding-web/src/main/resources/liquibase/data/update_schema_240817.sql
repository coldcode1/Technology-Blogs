drop table column_article;
drop table column_info;

truncate table article;
INSERT INTO `article` (`id`, `user_id`, `article_type`, `title`, `short_title`, `picture`, `summary`, `category_id`, `source`, `source_url`, `offical_stat`, `topping_stat`, `cream_stat`, `status`, `deleted`, `create_time`, `update_time`) VALUES (2423036595302401, 1, 1, 'RabbitMQ实战--代码实现queue、exchange的申明', '', '', 'rabbiMQ实战: queue初始配置、Topic的exchange实现通知消息', 1, 2, '', 0, 0, 0, 1, 0, '2024-08-17 17:41:40', '2024-08-17 17:41:52');
INSERT INTO `article` (`id`, `user_id`, `article_type`, `title`, `short_title`, `picture`, `summary`, `category_id`, `source`, `source_url`, `offical_stat`, `topping_stat`, `cream_stat`, `status`, `deleted`, `create_time`, `update_time`) VALUES (2423036595302402, 1, 1, '技术博客园-如何保证高可用性', '', '', '在技术博客园中，如何保证高可用性', 1, 2, '', 0, 0, 0, 1, 0, '2024-08-17 18:04:09', '2024-08-17 18:04:09');
