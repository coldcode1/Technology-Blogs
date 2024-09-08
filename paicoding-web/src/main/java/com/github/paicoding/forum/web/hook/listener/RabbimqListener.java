package com.github.paicoding.forum.web.hook.listener;

import com.github.paicoding.forum.api.model.enums.NotifyTypeEnum;
import com.github.paicoding.forum.core.bo.MailBO;
import com.github.paicoding.forum.core.cache.RedisClient;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.core.common.MsgLogStatuesConstants;
import com.github.paicoding.forum.core.config.RabbitmqProperties;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqConnection;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqConnectionPool;
import com.github.paicoding.forum.core.util.EmailUtil;
import com.github.paicoding.forum.core.util.JsonUtil;
import com.github.paicoding.forum.service.notify.service.NotifyService;
import com.github.paicoding.forum.service.rabbitmqmsg.repository.entity.MsgLogDO;
import com.github.paicoding.forum.service.rabbitmqmsg.service.MsgLogService;
import com.github.paicoding.forum.service.statistics.constants.CountConstants;
import com.github.paicoding.forum.service.user.repository.entity.UserFootDO;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ConditionalOnProperty(value = "rabbitmq.switchFlag")
@Component
@Slf4j
public class RabbimqListener {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private RabbitmqProperties rabbitmqProperties;

    @Autowired
    private MsgLogService msgLogService;

    @PostConstruct
    private void init() throws IOException, InterruptedException, TimeoutException {
        String host = rabbitmqProperties.getHost();
        Integer port = rabbitmqProperties.getPort();
        String userName = rabbitmqProperties.getUsername();
        String password = rabbitmqProperties.getPassport();
        String virtualhost = rabbitmqProperties.getVirtualhost();
        Integer poolSize = rabbitmqProperties.getPoolSize();

        RabbitmqConnectionPool.initRabbitmqConnectionPool(host, port, userName, password, virtualhost, poolSize);
        RabbitmqConnection connection = RabbitmqConnectionPool.getConnection();
        Channel channel = connection.getConnection().createChannel();

        // 声明文章点赞、收藏通知的交换机和队列
        channel.exchangeDeclare(CommonConstants.EXCHANGE_NOTIFY_TOPIC, BuiltinExchangeType.TOPIC, true, false, null);
        channel.queueDeclare(CommonConstants.QUERE_NAME_NOTIFY, true, false, false, null);
        channel.queueBind(CommonConstants.QUERE_NAME_NOTIFY, CommonConstants.EXCHANGE_NOTIFY_TOPIC, CommonConstants.QUERE_KEY_NOTIFY);

        // 声明邮件注册的
        channel.exchangeDeclare(CommonConstants.EXCHANGE_EMAIL_DIRECT, BuiltinExchangeType.DIRECT, true, false, null);
        channel.queueDeclare(CommonConstants.QUERE_NAME_EMAIL, true, false, false, null);
        channel.queueBind(CommonConstants.QUERE_NAME_EMAIL, CommonConstants.EXCHANGE_EMAIL_DIRECT, CommonConstants.QUERE_KEY_EMAIL);

        channel.close();

        RabbitmqConnectionPool.returnConnection(connection);
    }

    @RabbitListener(queues = CommonConstants.QUERE_NAME_NOTIFY)
    public void notifyMesSaveAndSubmit(String message,  @Header("amqp_receivedRoutingKey") String routingKey) {
        try {
            log.info("Consumer msg: {}", message);

            if(routingKey.equals(CommonConstants.QUERE_KEY_COLLECT)){
                UserFootDO foot = JsonUtil.toObj(message, UserFootDO.class);
                notifyService.saveArticleNotify(foot, NotifyTypeEnum.COLLECT);
                RedisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO + foot.getDocumentId(), CountConstants.COLLECTION_COUNT, 1);
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + foot.getDocumentUserId(), CountConstants.COLLECTION_COUNT, 1);
            }else if (routingKey.equals(CommonConstants.QUERE_KEY_PRAISE)){
                UserFootDO foot = JsonUtil.toObj(message, UserFootDO.class);
                notifyService.saveArticleNotify(foot, NotifyTypeEnum.PRAISE);
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + foot.getDocumentUserId(), CountConstants.PRAISE_COUNT, 1);
                RedisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO + foot.getDocumentId(), CountConstants.PRAISE_COUNT, 1);
            }
        } catch (Exception e) {
            log.info("错误信息:{}", e.getMessage());
        }
    }

    @RabbitListener(queues = CommonConstants.QUERE_NAME_EMAIL)
    public void rabbitmqSendEmail(String message, Channel channel,  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("Consumer msg: {}", message);
        MailBO mailBO = JsonUtil.toObj(message, MailBO.class);
        MsgLogDO msgLogDO = msgLogService.queryByMsgId(mailBO.getMsgId());
        log.info("msgLogDO:{}", msgLogDO);
        if(msgLogDO ==null || msgLogDO.getStatus().equals(MsgLogStatuesConstants.SUCCESS)){
            log.info("信息已经消费过");
            return;
        }
        if(EmailUtil.sendMail(mailBO.getTitle(), mailBO.getTo(), mailBO.getContent())){
            log.info("成功发送邮件");
            msgLogService.updateStatusByMsgId(mailBO.getMsgId(), MsgLogStatuesConstants.SUCCESS);
            // ack是必需的，因为设置了消息持久化，如果不啊ack，会导致消息堆积。
            channel.basicAck(deliveryTag, false);
        }else {
            log.info("发送邮件失败, 交由定时任务重试");
            msgLogService.updateStatusByMsgId(mailBO.getMsgId(), MsgLogStatuesConstants.FAIL);
            // Nack也是必需的，因为设置了消息持久化，
            channel.basicNack(deliveryTag, false, false);
        }
    }

}