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

    @Override
    public void run(ApplicationArguments args) throws Exception {

        AsyncUtil.execute(() -> rabbitmqService.processConsumerMsg());
    }
}
