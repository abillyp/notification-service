package com.notification.notification_service.message;

import com.notification.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("EMAIL")
@RequiredArgsConstructor
public class EmailSender implements MessageSender {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.email}")
    private String routingKeyEmail;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(Notification notification) {
            rabbitTemplate.convertAndSend(exchange, routingKeyEmail, notification);
    }
}
