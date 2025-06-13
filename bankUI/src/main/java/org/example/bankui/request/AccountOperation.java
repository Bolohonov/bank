package org.example.bankui.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountOperation {
    private String currency;
    private String login;
    private Double amount;
}
