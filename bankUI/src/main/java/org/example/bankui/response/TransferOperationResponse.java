package org.example.bankui.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferOperationResponse {
    private String fromLogin;
    private String toLogin;
    private String fromCurrency;
    private String toCurrency;
    private Double amount;
}
