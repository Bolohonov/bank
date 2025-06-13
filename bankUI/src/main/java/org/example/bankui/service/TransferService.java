package org.example.bankui.service;

import org.example.bankui.request.TransferOperation;
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
public class TransferService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private String transferServiceUrl;

    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public HttpResponseDto transfer(TransferOperation request) {
        try {
            return restClient.post()
                    .uri(transferServiceUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
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
