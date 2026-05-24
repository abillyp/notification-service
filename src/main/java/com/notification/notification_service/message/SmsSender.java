package com.notification.notification_service.message;

import com.notification.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("SMS")
@RequiredArgsConstructor
public class SmsSender implements MessageSender{

        @Value("${rabbitmq.exchange}")
        private String exchange;

        @Value("${rabbitmq.routing-keys.sms}")
        private String routingKeySms;

        private final RabbitTemplate rabbitTemplate;

        @Override
        public void send(Notification notification) {
            rabbitTemplate.convertAndSend(exchange, routingKeySms, notification);
        }
}
