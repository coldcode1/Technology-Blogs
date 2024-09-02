package com.github.paicoding.forum.web.scheduledtask;

import com.github.paicoding.forum.core.bo.MailBO;
import com.github.paicoding.forum.core.common.MsgLogStatuesConstants;
import com.github.paicoding.forum.core.util.JsonUtil;
import com.github.paicoding.forum.service.notify.service.RabbitmqService;
import com.github.paicoding.forum.service.rabbitmqmsg.repository.entity.MsgLogDO;
import com.github.paicoding.forum.service.rabbitmqmsg.service.MsgLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailTask {

    /**
     * 最大投递次数
     */
    private static final int MAX_TRY_COUNT = 2;
    private static final Logger log = LoggerFactory.getLogger(EmailTask.class);

    @Autowired
    private MsgLogService msgLogService;

    @Autowired
    private RabbitmqService rabbitmqService;

    // 每60s查询一次数据库，查询状态为失败的消息,每条失败信息最多重试2次
    @Scheduled(cron = "0/60 * * * * ?")
    private void run(){
        List<MsgLogDO> msgLogDOS = msgLogService.queryMsgByStatus(MsgLogStatuesConstants.FAIL);
        log.info("定时任务开始执行，查询到{}条消息记录", msgLogDOS.size());
        msgLogDOS.forEach(msgLogDO -> {
            if (msgLogDO.getTryCount() >= MAX_TRY_COUNT) {
                msgLogService.updateStatusByMsgId(msgLogDO.getMsgId(), MsgLogStatuesConstants.RETRY_FAIL);
                return;
            }
            rabbitmqService.publishMailerMsg(msgLogDO.getExchange(), msgLogDO.getRoutingKey(), JsonUtil.toObj(msgLogDO.getMsg(), MailBO.class));
            // 重新次数+1
            msgLogService.updateTryCount(msgLogDO.getMsgId(), msgLogDO.getTryCount() + 1);
        });
    }
}
