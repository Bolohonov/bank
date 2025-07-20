package org.example.exchangeservice.service;

import lombok.RequiredArgsConstructor;
import org.example.exchangeservice.mapping.RatesMapper;
import org.example.exchangeservice.response.CurrencyRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeConsumerService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeConsumerService.class);
    private final RatesService ratesService;
    private final RatesMapper ratesMapper;

    @KafkaListener(topics = "exchange", groupId = "currency-rate-group")
    public void consumeRate(List<LinkedHashMap> currencyRate) {
        log.info("Получено сообщение из топика 'exchange', количество курсов: {}", currencyRate.size());

        List<CurrencyRateResponse> currencyRateDtos = currencyRate.stream()
                .map(ratesMapper::toDto)
                .collect(Collectors.toList());

        ratesService.saveRates(currencyRateDtos);
    }
}
