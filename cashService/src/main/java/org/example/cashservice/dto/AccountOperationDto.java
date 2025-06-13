package org.example.cashservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountOperationDto {
    private String currency;
    private String login;
    private Double amount;
}
