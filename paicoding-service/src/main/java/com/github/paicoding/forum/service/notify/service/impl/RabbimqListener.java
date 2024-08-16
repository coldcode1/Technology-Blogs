package com.github.paicoding.forum.service.notify.service.impl;

import com.github.paicoding.forum.api.model.enums.NotifyTypeEnum;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.core.util.JsonUtil;
import com.github.paicoding.forum.service.notify.service.NotifyService;
import com.github.paicoding.forum.service.user.repository.entity.UserFootDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(value = "rabbitmq.switchFlag")
@Component
@Slf4j
public class RabbimqListener {

    @Autowired
    private NotifyService notifyService;

    @RabbitListener(queues = CommonConstants.QUERE_NAME_PRAISE)
    public void proposalSaveAndSubmit(String message) {
        try {
            log.info("Consumer msg: {}", message);
            notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.PRAISE);
        } catch (Exception e) {
            log.info("错误信息:{}", e.getMessage());
        }
    }

    @RabbitListener(queues = CommonConstants.QUERE_NAME_COLLECT)
    public void collectSaveAndSubmit(String message) {
        try {
            log.info("Consumer msg: {}", message);
            notifyService.saveArticleNotify(JsonUtil.toObj(message, UserFootDO.class), NotifyTypeEnum.COLLECT);
        } catch (Exception e) {
            log.info("错误信息:{}", e.getMessage());

        }
    }

}
