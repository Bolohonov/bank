package org.example.exchangeservice.service;

import org.example.exchangeservice.response.CurrencyResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    public List<CurrencyResponse> getCurrency() {
        return List.of(
                CurrencyResponse.builder().currency("USD").title("Доллар США").build(),
                CurrencyResponse.builder().currency("RUB").title("Российский рубль").build(),
                CurrencyResponse.builder().currency("EUR").title("Евро").build()
        );
    }

}
