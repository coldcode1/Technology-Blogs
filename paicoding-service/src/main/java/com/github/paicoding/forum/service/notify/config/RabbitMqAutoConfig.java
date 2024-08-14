package com.github.paicoding.forum.service.notify.config;

import com.github.paicoding.forum.core.async.AsyncUtil;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.core.config.RabbitmqProperties;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqConnection;
import com.github.paicoding.forum.core.rabbitmq.RabbitmqConnectionPool;
import com.github.paicoding.forum.service.notify.service.RabbitmqService;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author YiHui
 * @date 2023/6/9
 */
@Configuration
@ConditionalOnProperty(value = "rabbitmq.switchFlag")
@EnableConfigurationProperties(RabbitmqProperties.class)
public class RabbitMqAutoConfig implements ApplicationRunner {
    @Resource
    private RabbitmqService rabbitmqService;

    @Autowired
    private RabbitmqProperties rabbitmqProperties;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        String host = rabbitmqProperties.getHost();
        Integer port = rabbitmqProperties.getPort();
        String userName = rabbitmqProperties.getUsername();
        String password = rabbitmqProperties.getPassport();
        String virtualhost = rabbitmqProperties.getVirtualhost();
        Integer poolSize = rabbitmqProperties.getPoolSize();

        RabbitmqConnectionPool.initRabbitmqConnectionPool(host, port, userName, password, virtualhost, poolSize);
        RabbitmqConnection connection = RabbitmqConnectionPool.getConnection();
        Channel channel = connection.getConnection().createChannel();
        // 声明exchange中的消息为可持久化，不自动删除
        channel.exchangeDeclare(CommonConstants.EXCHANGE_NAME_DIRECT, BuiltinExchangeType.DIRECT, true, false, null);

        // 声明点赞的消息队列
        channel.queueDeclare(CommonConstants.QUERE_NAME_PRAISE, true, false, false, null);
        //绑定队列到交换机
        channel.queueBind(CommonConstants.QUERE_NAME_PRAISE, CommonConstants.EXCHANGE_NAME_DIRECT, CommonConstants.QUERE_KEY_PRAISE);

        // 声明收藏的消息队列
        channel.queueDeclare(CommonConstants.QUERE_NAME_COLLECT, true, false, false, null);
        //绑定队列到交换机
        channel.queueBind(CommonConstants.QUERE_NAME_COLLECT, CommonConstants.EXCHANGE_NAME_DIRECT, CommonConstants.QUERE_KEY_COLLECT);
        channel.close();

        RabbitmqConnectionPool.returnConnection(connection);



        AsyncUtil.execute(() -> rabbitmqService.processConsumerMsg());
    }
}
