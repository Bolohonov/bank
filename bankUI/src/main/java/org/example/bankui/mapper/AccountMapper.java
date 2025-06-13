package org.example.bankui.mapper;

import org.example.bankui.model.Account;
import org.example.bankui.response.AccountResponse;
import org.example.bankui.response.CurrencyResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AccountMapper {
    public Account toModel(AccountResponse accountResponse, List<CurrencyResponse> currencyResponses) {
        if (accountResponse == null) {
            return null;
        }

        String currencyTitle = findCurrencyTitleByCode(currencyResponses, accountResponse.getCurrency());

        return Account.builder()
                .accountNumber(accountResponse.getAccountNumber())
                .balance(accountResponse.getBalance())
                .currencyTitle(currencyTitle)
                .build();
    }

    private String findCurrencyTitleByCode(List<CurrencyResponse> currencyResponses, String currencyCode) {
        if (currencyCode == null || currencyResponses == null) {
            return null;
        }

        Optional<CurrencyResponse> currencyDtoOptional = currencyResponses.stream()
                .filter(currencyDto -> currencyCode.equals(currencyDto.getCurrency()))
                .findFirst();

        return currencyDtoOptional.map(CurrencyResponse::getTitle).orElse("");
    }
}
