package org.example.exchangeservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.example.exchangeservice.mapping.RatesMapper;
import org.example.exchangeservice.response.CurrencyRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private final MeterRegistry meterRegistry;

    @Value("${metricsEnabled:true}")
    private boolean metricsEnabled;

    @KafkaListener(topics = "exchange", groupId = "currency-rate-group")
    public void consumeRate(List<LinkedHashMap> currencyRate) {
        log.info("Получено сообщение из топика 'exchange', количество курсов: {}", currencyRate.size());

        List<CurrencyRateResponse> currencyRateDtos = currencyRate.stream()
                .map(ratesMapper::toDto)
                .collect(Collectors.toList());

        ratesService.saveRates(currencyRateDtos);
        if (metricsEnabled) meterRegistry.counter("currency_rates_get").increment();
    }
}
