package org.example.transferservice.service;

import org.example.transferservice.reponse.HttpResponseDto;
import org.example.transferservice.request.BlockerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;


@Service
public class BlockerApplicationService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private String blockerApplicationUrl;

    @Retryable(
        value = {ResourceAccessException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public HttpResponseDto checkBlocker(BlockerRequest request) {
        try {
            return restClient.post()
                    .uri(blockerApplicationUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось проверить подозрительную операцию. Причина: " + e.getMessage())
                    .build();
        }
    }

}
