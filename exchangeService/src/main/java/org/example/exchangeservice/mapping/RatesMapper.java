package org.example.exchangeservice.mapping;

import org.example.exchangeservice.model.Rate;
import org.example.exchangeservice.response.CurrencyRateResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Component
public class RatesMapper {

    public Rate toEntity(CurrencyRateResponse dto) {
        if (dto == null) {
            return null;
        }

        return Rate.builder()
                .currency(dto.getCurrency())
                .buy(BigDecimal.valueOf(dto.getBuy()))
                .sale(BigDecimal.valueOf(dto.getSale()))
                .build();
    }

    public CurrencyRateResponse toDto(Rate rate) {
        if (rate == null) {
            return null;
        }

        return CurrencyRateResponse.builder()
                .currency(rate.getCurrency())
                .buy(rate.getBuy().doubleValue())
                .sale(rate.getSale().doubleValue())
                .build();
    }

    public CurrencyRateResponse toDto(LinkedHashMap rate) {
        if (rate == null) {
            return null;
        }

        return CurrencyRateResponse.builder()
                .currency((String) rate.get("currency"))
                .buy(((Double) rate.get("buy")).doubleValue())
                .sale(((Double) rate.get("sale")).doubleValue())
                .build();
    }
}
