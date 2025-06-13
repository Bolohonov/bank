package org.example.bankui.mapper;

import org.example.bankui.model.Currency;
import org.example.bankui.response.CurrencyResponse;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {
    public Currency toModel(CurrencyResponse currencyResponse) {
        if (currencyResponse == null) {
            return null;
        }

        return Currency.builder()
                .currencyCode(currencyResponse.getCurrency()) // Маппинг поля currency -> currencyCode
                .title(currencyResponse.getTitle())
                .build();
    }
}
