package org.example.transferservice.service;

import org.example.transferservice.reponse.HttpResponseDto;
import org.example.transferservice.reponse.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;


@Service
public class NotificationService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private String notificationsUrl;

    @Retryable(
        value = {ResourceAccessException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public HttpResponseDto sendNotification(String email, String message) {

        NotificationResponse response = NotificationResponse.builder()
                .application("Cash")
                .email(email)
                .message(message)
                .build();

        try {
            return restClient.post()
                    .uri(notificationsUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response)
                    .retrieve()
                    .body(HttpResponseDto.class); // Десериализуем ответ в HttpResponseDto
        } catch (Exception e) {
            // Если сервис недоступен, выводим сообщение об ошибке
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось отправить уведомление. Причина: " + e.getMessage())
                    .build();
        }
    }

}
