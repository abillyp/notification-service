package com.notification.notification_service.message;

import com.notification.notification_service.model.Notification;

public interface MessageSender {

    void send(Notification notification);
}
