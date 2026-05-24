package com.notification.notification_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationResponse(UUID id,
                                   String type,
                                   String recipient,
                                   String status,
                                   LocalDateTime createdAt) {
}
