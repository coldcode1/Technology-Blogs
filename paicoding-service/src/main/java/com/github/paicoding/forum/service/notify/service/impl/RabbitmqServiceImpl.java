package com.github.paicoding.forum.service.notify.service.impl;

import com.github.paicoding.forum.api.model.enums.NotifyTypeEnum;
import com.github.paicoding.forum.core.bo.MailBO;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.core.common.MsgLogStatuesConstants;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqConnection;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqConnectionPool;
import com.github.paicoding.forum.core.util.JsonUtil;
import com.github.paicoding.forum.core.util.SpringUtil;
import com.github.paicoding.forum.service.notify.service.NotifyService;
import com.github.paicoding.forum.service.notify.service.RabbitmqService;
import com.github.paicoding.forum.service.rabbitmqmsg.service.MsgLogService;
import com.github.paicoding.forum.service.user.repository.entity.UserFootDO;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitmqServiceImpl implements RabbitmqService {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private MsgLogService msgLogService;


    @Override
    public boolean enabled() {
        return "true".equalsIgnoreCase(SpringUtil.getConfig("rabbitmq.switchFlag"));
    }

    @Override
    public void publishMsg(String exchange,
                           BuiltinExchangeType exchangeType,
                           String toutingKey,
                           String message) {
        try {
            //创建连接
            RabbitmqConnection rabbitmqConnection = RabbitmqConnectionPool.getConnection();
            Connection connection = rabbitmqConnection.getConnection();
            //创建消息通道
            Channel channel = connection.createChannel();

            // 发布消息
            channel.basicPublish(exchange, toutingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            log.info("Publish msg: {}", message);
            closeChannelAndConnection(channel, rabbitmqConnection);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void publishMailerMsg(String exchange,
                                 String routingKey,
                                 MailBO mailBO) {

        String message = JsonUtil.toStr(mailBO);

        // 最多重试2次
        int retryCount = 0;
        int maxRetryCount = 2;
        boolean isOk = false;
        Channel channel = null;
        RabbitmqConnection rabbitmqConnection = null;
        while(retryCount < maxRetryCount && !isOk){
            try {
                retryCount++;
                // 创建连接
                rabbitmqConnection = RabbitmqConnectionPool.getConnection();
                Connection connection = rabbitmqConnection.getConnection();
                // 创建消息通道
                channel = connection.createChannel();
                // 开启发布确认模式
                channel.confirmSelect();
                // 消息可靠性！：发布消息，并且进行了信息持久化！
                channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                log.info("Publish msg: {}", message);

                // 等待确认消息发布成功
                if (channel.waitForConfirms()) {
                    log.info("Message publish confirmed");
                    // 存储于数据库中。保证唯一索引。
                    msgLogService.saveMsgLog(mailBO.getMsgId(), CommonConstants.EXCHANGE_EMAIL_DIRECT, CommonConstants.QUERE_KEY_EMAIL, message, MsgLogStatuesConstants.WAIT);
                    isOk = true;
                } else {
                    log.error("Message publish failed");
                }
            } catch (InterruptedException | IOException e) {
                log.error("Failed to publish message", e);
            } finally {
                closeChannelAndConnection(channel, rabbitmqConnection);
            }
        }
        if (!isOk) {
            log.error("Failed to publish message after {} retries", maxRetryCount);
            // todo 更新数据库设置为失败，交给定时任务处理
            msgLogService.updateStatusByMsgId(message, MsgLogStatuesConstants.FAIL);
        }
    }

    private void closeChannelAndConnection(Channel channel, RabbitmqConnection rabbitmqConnection) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if(rabbitmqConnection != null) {
                RabbitmqConnectionPool.returnConnection(rabbitmqConnection);
            }
        } catch (IOException | TimeoutException e) {
            log.error("Failed to close channel or connection", e);
        }
    }


    @Override
    public void consumerMsg(String exchange,
                            String queueName,
                            String routingKey) {

        try {
            //创建连接
            RabbitmqConnection rabbitmqConnection = RabbitmqConnectionPool.getConnection();
            Connection connection = rabbitmqConnection.getConnection();
            //创建消息信道
            final Channel channel = connection.createChannel();
            //消息队列
            channel.queueDeclare(queueName, true, false, false, null);
            //绑定队列到交换机
            channel.queueBind(queueName, exchange, routingKey);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    log.info("Consumer msg: {}", message);

                    // 获取Rabbitmq消息，并保存到DB
                    // 说明：这里仅作为示例，如果有多种类型的消息，可以根据消息判定，简单的用 if...else 处理，复杂的用工厂 + 策略模式
                    notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.PRAISE);

                    channel.basicAck(envelope.getDeliveryTag(), false);
                    try {
                        channel.close();
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                    RabbitmqConnectionPool.returnConnection(rabbitmqConnection);
                }
            };
            // 取消自动ack
            channel.basicConsume(queueName, false, consumer);

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processConsumerMsg() {
        log.info("Begin to processConsumerMsg.");

        Integer stepTotal = 1;
        Integer step = 0;

        // TODO: 这种方式非常 Low，后续会改造成阻塞 I/O 模式
        while (true) {
            step++;
            try {
                log.info("processConsumerMsg cycle.");
//                consumerMsg(CommonConstants.EXCHANGE_NAME_DIRECT, CommonConstants.QUERE_NAME_PRAISE,
//                        CommonConstants.QUERE_KEY_PRAISE);
                if (step.equals(stepTotal)) {
                    Thread.sleep(1000000);
                    step = 0;
                }
            } catch (Exception e) {

            }
        }
    }
}
