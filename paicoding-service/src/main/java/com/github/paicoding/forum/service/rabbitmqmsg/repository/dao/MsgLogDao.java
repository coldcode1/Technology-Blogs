package com.github.paicoding.forum.service.rabbitmqmsg.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.paicoding.forum.service.rabbitmqmsg.repository.entity.MsgLogDO;
import com.github.paicoding.forum.service.user.repository.mapper.MsgLogMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public MsgLogDO queryByMsgId(String msgId) {
        return this.getById(msgId);
    }

    public List<MsgLogDO> queryMsgByStatus(Integer status) {
        LambdaQueryWrapper<MsgLogDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MsgLogDO::getStatus, status);
        return this.list(queryWrapper);
    }

    public void updateTryCount(String  msgId, Integer tryCount) {
        MsgLogDO msgLogDO = new MsgLogDO();
        msgLogDO.setMsgId(msgId);
        msgLogDO.setTryCount(tryCount);
        this.updateById(msgLogDO);
    }
}
