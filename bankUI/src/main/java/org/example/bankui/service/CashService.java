package org.example.bankui.service;

import org.example.bankui.request.AccountOperation;
import org.example.bankui.response.HttpResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
public class CashService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private String cashServiceUrl;

    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public HttpResponseDto cashIn(AccountOperation request) {
        try {
            return restClient.post()
                    .uri(cashServiceUrl + "/cash/in")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось пополнить счет клиента. Причина: " + e.getMessage())
                    .build();
        }
    }

    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,                        // Максимальное количество попыток
            backoff = @Backoff(delay = 1000)        // Задержка между попытками (в миллисекундах)
    )
    public HttpResponseDto cashOut(AccountOperation request) {
        try {
            return restClient.post()
                    .uri(cashServiceUrl + "/cash/out")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось списать ДС со счета клиента. Причина: " + e.getMessage())
                    .build();
        }
    }
}
