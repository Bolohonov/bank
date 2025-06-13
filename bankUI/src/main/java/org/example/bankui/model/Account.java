package org.example.bankui.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {
    private String accountNumber;
    private String currencyTitle;
    private Double balance;

}
