package com.notification.notification_service.message;

import com.notification.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailNotificationProcessor emailNotificationProcessor;

    @RabbitListener(queues = "${rabbitmq.queues.email}")
    void consumeEmailNotification(Notification notification){
        emailNotificationProcessor.consumerRetryable(notification);
    }
}
