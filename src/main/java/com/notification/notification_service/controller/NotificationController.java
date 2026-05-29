package com.notification.notification_service.controller;

import com.notification.notification_service.dto.NotificationDetailsResponse;
import com.notification.notification_service.dto.NotificationRequest;
import com.notification.notification_service.dto.NotificationResponse;
import com.notification.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService  notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse  sendNotification(@RequestBody @Valid NotificationRequest notificationRequest){
            return notificationService.sendNotification(notificationRequest);

    }

    @GetMapping("/{id}")
    public NotificationDetailsResponse getNotificationById(@PathVariable UUID id){
        return notificationService.getNotification(id);
    }

    @GetMapping
    public Page<NotificationDetailsResponse> getNotificationByFilter(@RequestParam(required = false) String status,
                                                                     @RequestParam(required = false) String type,
                                                                     @RequestParam(required = false) String recipient,
                                                                     Pageable pageable){
        return notificationService.getNotifications(status, type, recipient, pageable);
    }

}
