package com.github.paicoding.forum.service.notify.service.impl;

import com.github.paicoding.forum.api.model.enums.NotifyTypeEnum;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.core.config.RabbitmqProperties;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqConnection;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqConnectionPool;
import com.github.paicoding.forum.core.util.JsonUtil;
import com.github.paicoding.forum.service.notify.service.NotifyService;
import com.github.paicoding.forum.service.notify.service.RabbitmqService;
import com.github.paicoding.forum.service.user.repository.entity.UserFootDO;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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

        channel.exchangeDeclare(CommonConstants.EXCHANGE_NAME_TOPIC, BuiltinExchangeType.TOPIC, true, false, null);
        channel.queueDeclare(CommonConstants.QUERE_NAME_NOTIFY, true, false, false, null);
        channel.queueBind(CommonConstants.QUERE_NAME_NOTIFY, CommonConstants.EXCHANGE_NAME_TOPIC, CommonConstants.QUERE_KEY_NOTIFY);

        channel.close();

        RabbitmqConnectionPool.returnConnection(connection);
    }

    @RabbitListener(queues = CommonConstants.QUERE_NAME_NOTIFY)
    public void notifyMesSaveAndSubmit(String message,  @Header("amqp_receivedRoutingKey") String routingKey) {
        try {
            log.info("Consumer msg: {}", message);

            if(routingKey.equals(CommonConstants.QUERE_KEY_COLLECT)){
                notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.COLLECT);
            }else if (routingKey.equals(CommonConstants.QUERE_KEY_PRAISE)){
                notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.PRAISE);
            }
        } catch (Exception e) {
            log.info("错误信息:{}", e.getMessage());
        }
    }

}
