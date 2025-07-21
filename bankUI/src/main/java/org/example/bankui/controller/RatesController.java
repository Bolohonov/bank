package org.example.bankui.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.example.bankui.response.CurrencyRateResponse;
import org.example.bankui.service.ExchangeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class RatesController {

    private final MeterRegistry meterRegistry;
    @Value("${metricsEnabled:true}")
    private boolean metricsEnabled;
    private final ExchangeService exchangeService;

    @GetMapping("/api/rates")
    @Secured("ROLE_USER")
    public List<CurrencyRateResponse> getRates() {
        if (metricsEnabled) {
            Timer.Sample timer = Timer.start(meterRegistry);
            try {
                // Эмуляция случайной задержки от 50 до 200 мс, чтобы ловить алерты
                long delay = 50 + new Random().nextInt(150);
                Thread.sleep(delay);

                return exchangeService.getRates();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted during sleep", e);
            } finally {
                timer.stop(meterRegistry.timer("bank_ui_get_rates_duration"));
            }
        } else {
            return exchangeService.getRates();
        }
    }
}
