package org.example.bankui.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountOperationResponse {
    private String currency;
    private String login;
    private Double amount;
}
