package com.github.paicoding.forum.service.rabbitmqmsg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.paicoding.forum.service.rabbitmqmsg.repository.dao.MsgLogDao;
import com.github.paicoding.forum.service.rabbitmqmsg.repository.entity.MsgLogDO;
import com.github.paicoding.forum.service.rabbitmqmsg.service.MsgLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsgLogServiceImpl implements MsgLogService {

    @Autowired
    private MsgLogDao msgLogDao;

    @Override
    public void saveMsgLog(String Id, String exchange, String routingKey, String msg, Integer status) {
        msgLogDao.saveMsgLog(Id, exchange, routingKey, msg, status);
    }

    @Override
    public void updateStatusByMsgId(String Id, Integer status) {
        msgLogDao.updateStatus(Id, status);
    }

    @Override
    public MsgLogDO queryByMsgId(String msgId) {
        return msgLogDao.queryByMsgId(msgId);
    }

    @Override
    public List<MsgLogDO> queryMsgByStatus(Integer status) {
        return msgLogDao.queryMsgByStatus(status);
    }

    @Override
    public void updateTryCount(String msgId, Integer tryCount) {
        msgLogDao.updateTryCount(msgId, tryCount);
    }
}
