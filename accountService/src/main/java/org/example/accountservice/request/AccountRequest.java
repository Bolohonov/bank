package org.example.accountservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountRequest {
    private String currency;
    private String login;
}
