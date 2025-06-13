package org.example.accountservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountOperation {
    private String currency;
    private String login;
    private Double amount;
}
