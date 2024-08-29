CREATE TABLE `ceshi`
(
    `id`           int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`      int unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
    `article_type` tinyint      NOT NULL DEFAULT '1' COMMENT '文章类型：1-博文，2-问答',
    `title`        varchar(120) NOT NULL DEFAULT '' COMMENT '文章标题',
    `short_title`  varchar(120) NOT NULL DEFAULT '' COMMENT '短标题',
    `picture`      varchar(128) NOT NULL DEFAULT '' COMMENT '文章头图',
    `summary`      varchar(300) NOT NULL DEFAULT '' COMMENT '文章摘要',
    `category_id`  int unsigned NOT NULL DEFAULT '0' COMMENT '类目ID',
    `source`       tinyint      NOT NULL DEFAULT '1' COMMENT '来源：1-转载，2-原创，3-翻译',
    `source_url`   varchar(128) NOT NULL DEFAULT '1' COMMENT '原文链接',
    `offical_stat` int unsigned NOT NULL DEFAULT '0' COMMENT '官方状态：0-非官方，1-官方',
    `topping_stat` int unsigned NOT NULL DEFAULT '0' COMMENT '置顶状态：0-不置顶，1-置顶',
    `cream_stat`   int unsigned NOT NULL DEFAULT '0' COMMENT '加精状态：0-不加精，1-加精',
    `status`       tinyint      NOT NULL DEFAULT '0' COMMENT '状态：0-未发布，1-已发布',
    `deleted`      tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    KEY            `idx_category_id` (`category_id`),
    KEY `idx_title` (`title`),
    KEY `idx_short_title` (`short_title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='文章表';