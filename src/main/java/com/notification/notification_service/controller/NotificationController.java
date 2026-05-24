package com.notification.notification_service.controller;

import com.notification.notification_service.dto.NotificationRequest;
import com.notification.notification_service.dto.NotificationResponse;
import com.notification.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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


}
