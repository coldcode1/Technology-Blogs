package com.github.paicoding.forum.service.rabbitmqmsg.service;

public interface MsgLogService {
    void saveMsgLog(String Id, String exchange, String routingKey, String msg, Integer status);

    void updateStatus(String Id, Integer status);
}
