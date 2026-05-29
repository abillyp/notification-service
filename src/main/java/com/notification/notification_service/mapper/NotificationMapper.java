package com.notification.notification_service.mapper;

import com.notification.notification_service.dto.NotificationDetailsResponse;
import com.notification.notification_service.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    public NotificationDetailsResponse toDetailsResponse(Notification notification);
}
