package com.github.paicoding.forum.service.rabbitmqmsg.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("msg_log")
public class MsgLogDO {

    @TableId(value = "msgId")
    private String msgId;

    private String msg;

    private String exchange;

    private String routingKey;

    private Integer status;

    private Integer tryCount;

    private Date nextTryTime;

    private Date createTime;

    private Date updateTime;

}
