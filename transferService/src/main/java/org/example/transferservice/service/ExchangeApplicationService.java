package org.example.transferservice.service;

import org.example.transferservice.reponse.CurrencyRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;


@Service
public class ExchangeApplicationService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private String exchangeApplicationUrl;

    @Retryable(
        value = {ResourceAccessException.class}, // Повторять при ошибках соединения
        maxAttempts = 3,                        // Максимальное количество попыток
        backoff = @Backoff(delay = 1000)        // Задержка между попытками (в миллисекундах)
    )
    public List<CurrencyRateResponse> getRates() {
        try {
            return restClient.get()
                    .uri(exchangeApplicationUrl+"/rates")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CurrencyRateResponse>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
