package org.example.transferservice.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountOperationRequest {
    private String currency;
    private String login;
    private Double amount;
}
