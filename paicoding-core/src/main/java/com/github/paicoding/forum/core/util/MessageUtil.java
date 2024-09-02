package com.github.paicoding.forum.core.util;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;

/**
 * <p>
 * MessageHelper
 * </p>
 *
 * @author panzhi
 * @since 2024/7/8
 */
public class MessageUtil {

    /**
     * 将对象序列化成消息数据
     * @param obj
     * @return
     */
    public static Message objToMsg(Object obj) {
        if (null == obj) {
            return null;
        }
        if(obj instanceof String){

        }

        Message message = MessageBuilder.withBody(JsonUtil.toStr(obj).getBytes()).build();
        // 设置消息持久化
        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        // 设置消息为json格式
        message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
        return message;
    }

    /**
     * 将消息数据反序列化成对象
     * @param message
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T msgToObj(Message message, Class<T> clazz) {
        if (null == message || null == clazz) {
            return null;
        }

        String json = new String(message.getBody());
        return JsonUtil.strToObj(json, clazz);
    }
}
