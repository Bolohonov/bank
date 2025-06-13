package org.example.bankui.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {
    private String accountNumber;
    private String currency;
    private Double balance;
}
