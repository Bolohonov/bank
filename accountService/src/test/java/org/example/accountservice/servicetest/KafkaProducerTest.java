package org.example.accountservice.servicetest;

import org.example.accountservice.AccountServiceApplication;
import org.example.accountservice.TestSecurityConfig;
import org.example.accountservice.notifications.NotificationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

@SpringBootTest(classes = {AccountServiceApplication.class, TestSecurityConfig.class})
@TestPropertySource(locations = "classpath:application.yml")
@ActiveProfiles("test")
@EmbeddedKafka(topics = {"notifications"})
public class KafkaProducerTest {

    @Autowired
    private KafkaTemplate<String, NotificationDto> kafkaTemplate;

    @Test
    public void testProcessor(){
        NotificationDto notificationDto = NotificationDto.builder()
                .email("test@mail.ru")
                .message("Тестовое сообщение")
                .application("account")
                .build();

        String key = UUID.randomUUID().toString();
        kafkaTemplate.send("notifications", key, notificationDto);

    }


}
