package org.example.exchangeservice.configuration;

import lombok.RequiredArgsConstructor;
import org.example.exchangeservice.response.CurrencyRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Autowired
    private final KafkaProperties kafkaProperties;

    @Bean
    public DefaultKafkaConsumerFactory<String, List<CurrencyRateResponse>> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(null),
                new StringDeserializer(),
                new JsonDeserializer<>(new com.fasterxml.jackson.core.type.TypeReference<List<CurrencyRateResponse>>() {})
        );
    }
}
