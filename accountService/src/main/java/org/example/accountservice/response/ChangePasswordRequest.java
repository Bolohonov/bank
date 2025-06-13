package org.example.accountservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChangePasswordRequest {
    private String login;
    private String password;
}
