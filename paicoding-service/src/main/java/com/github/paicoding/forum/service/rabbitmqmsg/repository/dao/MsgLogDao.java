package com.github.paicoding.forum.service.rabbitmqmsg.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.paicoding.forum.service.rabbitmqmsg.repository.entity.MsgLogDO;
import com.github.paicoding.forum.service.user.repository.mapper.MsgLogMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MsgLogDao extends ServiceImpl<MsgLogMapper, MsgLogDO> {

    public void saveMsgLog(String id,String exchange, String routingKey, String msg, Integer status) {
        MsgLogDO msgLogDO = new MsgLogDO();
        msgLogDO.setMsgId(id);
        msgLogDO.setExchange(exchange);
        msgLogDO.setRoutingKey(routingKey);
        msgLogDO.setMsg(msg);
        msgLogDO.setStatus(status);
        this.save(msgLogDO);
    }

    public void updateStatus(String id, Integer status) {
        MsgLogDO msgLogDO = new MsgLogDO();
        msgLogDO.setMsgId(id);
        msgLogDO.setStatus(status);
        this.updateById(msgLogDO);
    }

}
