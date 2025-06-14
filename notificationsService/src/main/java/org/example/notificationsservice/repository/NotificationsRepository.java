package org.example.notificationsservice.repository;

import org.example.notificationsservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notification, Long> {
}
