package org.example.bankui.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferOperation {
    private String fromLogin;
    private String toLogin;
    private String fromCurrency;
    private String toCurrency;
    private Double amount;
}
