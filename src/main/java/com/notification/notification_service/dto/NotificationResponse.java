package com.notification.notification_service.dto;

import com.notification.notification_service.enums.NotificationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationResponse(UUID id,
                                   String type,
                                   String recipient,
                                   NotificationStatus status,
                                   LocalDateTime createdAt) {
}
