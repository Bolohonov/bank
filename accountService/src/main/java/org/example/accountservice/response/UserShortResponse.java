package org.example.accountservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserShortResponse {
    private String login;
    private String fio;
    private String email;
    private String dateOfBirth;
}
