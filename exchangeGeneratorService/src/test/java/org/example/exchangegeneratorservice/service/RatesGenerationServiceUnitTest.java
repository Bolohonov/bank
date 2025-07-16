package org.example.exchangegeneratorservice.service;

import org.example.exchangegeneratorservice.ExchangeGeneratorServiceApplication;
import org.example.exchangegeneratorservice.TestSecurityConfig;
import org.example.exchangegeneratorservice.dto.HttpResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ExchangeGeneratorServiceApplication.class, TestSecurityConfig.class})
@TestPropertySource(locations = "classpath:application.yml")
@ActiveProfiles("test")
public class RatesGenerationServiceUnitTest {

    @Autowired
    private RatesGenerationService ratesGenerationService;

    @MockitoBean(reset = MockReset.BEFORE)
    private ExchangeService exchangeService;

    @MockitoBean(reset = MockReset.BEFORE)
    private ExchangeProducer exchangeProducer;

    @Test
    void testGenerateRandomRates() {
        ratesGenerationService.generateRates();

        HttpResponseDto mockResponse = HttpResponseDto.builder()
                .statusCode("0")
                .statusMessage("Rates sent successfully")
                .build();

        when(exchangeApplicationService.sendRates(anyList())).thenReturn(mockResponse);
        when(exchangeProducer.sendRates(anyList())).thenReturn(mockResponse);

        verify(exchangeProducer, times(1)).sendRates(anyList());
    }
}
