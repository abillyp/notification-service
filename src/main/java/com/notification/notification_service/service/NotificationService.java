package com.notification.notification_service.service;

import tools.jackson.databind.ObjectMapper;
import com.notification.notification_service.dto.NotificationRequest;
import com.notification.notification_service.dto.NotificationResponse;
import com.notification.notification_service.message.MessageSender;
import com.notification.notification_service.model.Notification;
import com.notification.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository  notificationRepository;

    private final Map<String, MessageSender> senders;

    private final ObjectMapper objectMapper;


    public NotificationResponse sendNotification(NotificationRequest notificationRequest) {

        Notification notification = Notification.builder()
                .type(notificationRequest.type())
                .recipient(notificationRequest.recipient())
                .subject(notificationRequest.subject())
                .body(notificationRequest.body())
                .priority(notificationRequest.priority())
                .metadata(objectMapper.writeValueAsString(notificationRequest.metadata()))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        senders.get(notificationRequest.type()).send(saved);

        return new NotificationResponse(saved.getId(), saved.getType(), saved.getRecipient(), saved.getStatus(), saved.getCreatedAt());

    }
}
