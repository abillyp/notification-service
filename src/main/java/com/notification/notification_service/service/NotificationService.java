package com.notification.notification_service.service;

import com.notification.notification_service.dto.NotificationDetailsResponse;
import com.notification.notification_service.enums.NotificationStatus;
import com.notification.notification_service.exception.NotificationProcessingException;
import com.notification.notification_service.mapper.NotificationMapper;
import com.notification.notification_service.specification.NotificationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import tools.jackson.databind.ObjectMapper;
import com.notification.notification_service.dto.NotificationRequest;
import com.notification.notification_service.dto.NotificationResponse;
import com.notification.notification_service.message.MessageSender;
import com.notification.notification_service.model.Notification;
import com.notification.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository  notificationRepository;
    private final Map<String, MessageSender> senders;
    private final ObjectMapper objectMapper;
    private final NotificationMapper mapper;


    public NotificationResponse sendNotification(NotificationRequest notificationRequest) {

        Notification notification = Notification.builder()
                .type(notificationRequest.type())
                .recipient(notificationRequest.recipient())
                .subject(notificationRequest.subject())
                .body(notificationRequest.body())
                .priority(notificationRequest.priority())
                .metadata(objectMapper.writeValueAsString(notificationRequest.metadata()))
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        senders.get(notificationRequest.type()).send(saved);

        return new NotificationResponse(saved.getId(), saved.getType(), saved.getRecipient(), saved.getStatus(), saved.getCreatedAt());

    }

    public NotificationDetailsResponse getNotification(UUID id) {
        return notificationRepository.findById(id)
                .map(mapper::toDetailsResponse)
                .orElseThrow(() -> new NotificationProcessingException("Msg não encontrada", new RuntimeException()));

    }

    public Page<NotificationDetailsResponse> getNotifications(String status, String type, String recipient,
                                                              Pageable pageable) {
        Specification<Notification> specification = Specification
                .where(NotificationSpecification.hasRecipient(recipient))
                .and(NotificationSpecification.hasType(type))
                .and(NotificationSpecification.hasStatus(status));
        Page<Notification> notifications = notificationRepository.findAll(specification, pageable);
        return notifications.map(mapper::toDetailsResponse);
    }
}
