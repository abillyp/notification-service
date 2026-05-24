package com.notification.notification_service.exception;


public class NotificationProcessingException extends RuntimeException{

    public NotificationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
