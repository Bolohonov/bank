package org.example.accountservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequest {
    private String login;
    private String password;
    private String fio;
    private String role;
    private String email;
    private String dateOfBirth;
}
