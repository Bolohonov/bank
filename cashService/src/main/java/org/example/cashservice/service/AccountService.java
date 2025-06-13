package org.example.cashservice.service;

import org.example.cashservice.dto.AccountOperationDto;
import org.example.cashservice.dto.HttpResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;



@Service
public class AccountService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private String accountServiceUrl;

    @Retryable(
            value = {ResourceAccessException.class}, // Повторять при ошибках соединения
            maxAttempts = 3,                        // Максимальное количество попыток
            backoff = @Backoff(delay = 1000)        // Задержка между попытками (в миллисекундах)
    )
    public HttpResponseDto cashIn(AccountOperationDto accountOperationDto) {
        try {
            return restClient.put()
                    .uri(accountServiceUrl + "/account/cashIn")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(accountOperationDto)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось выполнить операцию. Причина: " + e.getMessage())
                    .build();
        }
    }

    @Retryable(
            value = {ResourceAccessException.class}, // Повторять при ошибках соединения
            maxAttempts = 3,                        // Максимальное количество попыток
            backoff = @Backoff(delay = 1000)        // Задержка между попытками (в миллисекундах)
    )
    public HttpResponseDto cashOut(AccountOperationDto accountOperationDto) {
        try {
            return restClient.put()
                    .uri(accountServiceUrl + "/account/cashOut")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(accountOperationDto)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось выполнить операцию. Причина: " + e.getMessage())
                    .build();
        }
    }
}
