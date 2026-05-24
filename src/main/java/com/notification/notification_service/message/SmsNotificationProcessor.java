package com.notification.notification_service.message;

import com.notification.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsNotificationProcessor {

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000), retryFor = Exception.class)
    void consumerRetryable(Notification notification) {
        log.info("Processing SMS notification id={} to={} subject={}",
                notification.getId(),
                notification.getRecipient(),
                notification.getSubject());


    }

    @Recover
    public void recover(Exception exception, Notification notification) {
        log.error("Failed to process SMS notification id={} after 3 attempts. Error: {}",
                notification.getId(),
                exception.getMessage());
    }
}
