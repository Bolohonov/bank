package org.example.accountservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private String login;
    private String password;
    private String fio;
    private String role;
    private String email;
    private String dateOfBirth;
    private String statusCode;
    private String statusMessage;
}
