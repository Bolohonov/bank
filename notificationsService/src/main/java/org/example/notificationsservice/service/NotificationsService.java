package org.example.notificationsservice.service;

import org.example.notificationsservice.mapper.NotificationMapper;
import org.example.notificationsservice.model.Notification;
import org.example.notificationsservice.repository.NotificationsRepository;
import org.example.notificationsservice.request.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationsService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private NotificationsRepository notificationsRepository;

    public Long sendNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        Notification saved = notificationsRepository.save(notification);
        return saved.getId();
    }

}
