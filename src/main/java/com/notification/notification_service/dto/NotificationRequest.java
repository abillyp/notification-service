package com.notification.notification_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record NotificationRequest(@NotBlank @Pattern(regexp = "EMAIL|SMS", message = "Tipo deve ser EMAIL ou SMS") String type,
                                  @NotBlank @Size(max = 255) String recipient,
                                  @NotBlank @Size(max = 150) String subject,
                                  @NotBlank @Size(max = 5000) String body,
                                  @NotBlank String priority,
                                  Map<String, String> metadata) {
}
