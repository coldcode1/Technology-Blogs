package com.github.paicoding.forum.service.rabbitmqmsg.service;

import com.github.paicoding.forum.service.rabbitmqmsg.repository.entity.MsgLogDO;

import java.util.List;

public interface MsgLogService {
    void saveMsgLog(String Id, String exchange, String routingKey, String msg, Integer status);

    void updateStatusByMsgId(String Id, Integer status);

    MsgLogDO queryByMsgId(String msgId);

    List<MsgLogDO> queryMsgByStatus(Integer status);

    void updateTryCount(String msgId, Integer tryCount);

}
