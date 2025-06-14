package org.example.notificationsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications_log", schema = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "application_name", nullable = false)
    private String applicationName;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "datetime_create", nullable = false)
    private LocalDateTime dateTimeCreate;

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", message='" + message + '\'' +
                ", dateTimeCreate=" + dateTimeCreate +
                '}';
    }
}
