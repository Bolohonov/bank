package org.example.exchangegeneratorservice.service;

import lombok.RequiredArgsConstructor;
import org.example.exchangegeneratorservice.dto.CurrencyRateDto;
import org.example.exchangegeneratorservice.dto.HttpResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExchangeProducer {

    private static final Logger log = LoggerFactory.getLogger(ExchangeProducer.class);

    @Autowired
    private final KafkaTemplate<String, List<CurrencyRateDto>> kafkaTemplate;

    public HttpResponseDto sendRates(List<CurrencyRateDto> rates) {
        log.info("Отправка данных курсов валют");
        try {
            String key = UUID.randomUUID().toString();
            kafkaTemplate.send("exchange",key, rates).get();
            return HttpResponseDto.builder()
                    .statusCode("200")
                    .statusMessage("OK")
                    .build();
        } catch (Exception e) {
            log.error("Возникла проблема при отправке сообщения",e);
            return HttpResponseDto.builder()
                    .statusCode("500")
                    .statusMessage("Не удалось отправить курсы валют. Причина: " + e.getMessage())
                    .build();
        }
    }
}
