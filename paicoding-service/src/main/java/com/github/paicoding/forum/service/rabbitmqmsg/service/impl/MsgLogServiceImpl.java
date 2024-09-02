package com.github.paicoding.forum.service.rabbitmqmsg.service.impl;

import com.github.paicoding.forum.service.rabbitmqmsg.repository.dao.MsgLogDao;
import com.github.paicoding.forum.service.rabbitmqmsg.service.MsgLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsgLogServiceImpl implements MsgLogService {

    @Autowired
    private MsgLogDao msgLogDao;

    @Override
    public void saveMsgLog(String Id, String exchange, String routingKey, String msg, Integer status) {
        msgLogDao.saveMsgLog(Id, exchange, routingKey, msg, status);
    }

    @Override
    public void updateStatus(String Id, Integer status) {
        msgLogDao.updateStatus(Id, status);
    }
}
