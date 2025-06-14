package org.example.notificationsservice.mapper;

import org.example.notificationsservice.request.NotificationRequest;
import org.example.notificationsservice.model.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequest notificationRequest) {
        if (notificationRequest == null) {
            return null;
        }

        return Notification.builder()
                .email(notificationRequest.getEmail())
                .applicationName(notificationRequest.getApplication())
                .message(notificationRequest.getMessage())
                .dateTimeCreate(LocalDateTime.now())
                .build();
    }

}
