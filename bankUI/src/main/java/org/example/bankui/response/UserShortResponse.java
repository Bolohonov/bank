package org.example.bankui.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortResponse {
    private String login;
    private String fio;
    private String email;
    private String dateOfBirth;
}
