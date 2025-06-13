package org.example.accountservice.mapper;

import org.example.accountservice.model.Account;
import org.example.accountservice.response.AccountResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toDto(Account account) {
        if (account == null) {
            return null;
        }

        return AccountResponse.builder()
                .balance(account.getBalance().doubleValue())
                .accountNumber(account.getNumber())
                .currency(account.getCurrency())
                .build();
    }
}
