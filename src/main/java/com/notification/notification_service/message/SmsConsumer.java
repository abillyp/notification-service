package com.notification.notification_service.message;

import com.notification.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsConsumer {

    private final SmsNotificationProcessor smsNotificationProcessor;

    @RabbitListener(queues = "${rabbitmq.queues.sms}")
    void consumeSmsNotification(Notification notification){
        smsNotificationProcessor.consumerRetryable(notification);
    }

}
