package org.example.bankui.service;

import org.example.bankui.request.AccountRequest;
import org.example.bankui.request.ChangePasswordRequest;
import org.example.bankui.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.example.bankui.response.*;

import java.util.Collections;
import java.util.List;


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
    public HttpResponseDto registerUser(UserResponse userResponse) {
        try {
            return restClient.post()
                    .uri(accountServiceUrl+"/user/create")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(userResponse)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось создать клинета. Причина: " + e.getMessage())
                    .build();
        }
    }

    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public UserResponse getUserInfo(String login) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(accountServiceUrl+"/user/findbylogin");
            builder.queryParam("login", login);

            String url = builder.toUriString();

            return restClient.get()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(UserResponse.class);
        } catch (Exception e) {
            return UserResponse.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось получить данные клиента. Причина: " + e.getMessage())
                    .build();
        }
    }

    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public List<UserShortResponse> getAllUsers() {
        try {
            return restClient.get()
                    .uri(accountServiceUrl+"/user/all")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserShortResponse>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Retryable(
            value = {ResourceAccessException.class}, // Повторять при ошибках соединения
            maxAttempts = 3,                        // Максимальное количество попыток
            backoff = @Backoff(delay = 1000)        // Задержка между попытками (в миллисекундах)
    )
    public HttpResponseDto changePassword(ChangePasswordRequest changePasswordRequest) {
        try {
            return restClient.post()
                    .uri(accountServiceUrl+"/user/changepassword")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(changePasswordRequest)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось создать клинета. Причина: " + e.getMessage())
                    .build();
        }
    }


    @Retryable(
            value = {ResourceAccessException.class}, // Повторять при ошибках соединения
            maxAttempts = 3,                        // Максимальное количество попыток
            backoff = @Backoff(delay = 1000)        // Задержка между попытками (в миллисекундах)
    )
    public HttpResponseDto changeInfo(UserResponse userResponse) {
        try {
            return restClient.post()
                    .uri(accountServiceUrl+"/user/edit")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(userResponse)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось создать клинета. Причина: " + e.getMessage())
                    .build();
        }
    }


    @Retryable(
            value = {ResourceAccessException.class}, // Повторять при ошибках соединения
            maxAttempts = 3,                        // Максимальное количество попыток
            backoff = @Backoff(delay = 1000)        // Задержка между попытками (в миллисекундах)
    )
    public HttpResponseDto accountAdd(AccountRequest accountRequest) {
        try {
            return restClient.post()
                    .uri(accountServiceUrl+"/account/add")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(accountRequest)
                    .retrieve()
                    .body(HttpResponseDto.class);
        } catch (Exception e) {
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось создать клинета. Причина: " + e.getMessage())
                    .build();
        }
    }

    @Retryable(
            value = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public List<AccountResponse> getUserAccountInfo(String login) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(accountServiceUrl+"/account/all");
            builder.queryParam("login", login);

            String url = builder.toUriString();

            return restClient.get()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AccountResponse>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
