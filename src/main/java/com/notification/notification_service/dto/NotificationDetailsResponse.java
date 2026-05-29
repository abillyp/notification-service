package com.notification.notification_service.dto;

import com.notification.notification_service.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDetailsResponse(UUID id,
                                          String type,
                                          String recipient,
                                          String subject,
                                          LocalDateTime sentAt,
                                          String errorMessage,
                                          NotificationStatus status,
                                          LocalDateTime createdAt
) {
}
