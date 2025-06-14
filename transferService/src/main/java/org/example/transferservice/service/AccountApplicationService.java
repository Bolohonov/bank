package org.example.transferservice.service;

import org.example.transferservice.reponse.HttpResponseDto;
import org.example.transferservice.request.AccountOperationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
public class AccountApplicationService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private String accountApplicationUrl;

    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public HttpResponseDto cashIn(AccountOperationRequest request) {
        try {
            return restClient.put()
                    .uri(accountApplicationUrl+"/account/cashIn")
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

    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public HttpResponseDto cashOut(AccountOperationRequest request) {
        try {
            return restClient.put()
                    .uri(accountApplicationUrl+"/account/cashOut")
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
