package org.example.accountservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AccountResponse {
    private String accountNumber;
    private String currency;
    private Double balance;
}
