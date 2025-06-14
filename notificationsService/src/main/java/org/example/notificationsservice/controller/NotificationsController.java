package org.example.notificationsservice.controller;

import org.example.notificationsservice.response.HttpResponseDto;
import org.example.notificationsservice.request.NotificationRequest;
import org.example.notificationsservice.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    @Autowired
    private NotificationsService notificationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("SCOPE_notifications.write")
    public HttpResponseDto createNotification(@RequestBody NotificationRequest notification) {
        Long notificationId = notificationsService.sendNotification(notification);
        return HttpResponseDto.builder()
                .notificationId(notificationId)
                .statusMessage("Notification send OK")
                .statusCode("0")
                .build();
    }

    @ExceptionHandler
    public HttpResponseDto handleException(Exception ex) {
        return HttpResponseDto.builder()
                .notificationId(null)
                .statusMessage(ex.getLocalizedMessage())
                .statusCode("999")
                .build();
    }

}
