package org.example.exchangegeneratorservice.configurations;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.exchangegeneratorservice.dto.CurrencyRateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Autowired
    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaTemplate<String, CurrencyRateDto> kafkaTemplate() {
        var producerFactory = new DefaultKafkaProducerFactory<String, CurrencyRateDto>(
                kafkaProperties.buildProducerProperties(null),
                new StringSerializer(),
                new JsonSerializer<CurrencyRateDto>()
        );
        KafkaTemplate<String, CurrencyRateDto> kafkaTemplate=new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setObservationEnabled(true);
        return kafkaTemplate;
    }

    @Bean
    public NewTopic topicNotifications() {
        return TopicBuilder.name("exchange")
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }
}
