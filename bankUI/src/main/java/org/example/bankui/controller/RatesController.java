package org.example.bankui.controller;

import org.example.bankui.response.CurrencyRateResponse;
import org.example.bankui.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RatesController {

    @Autowired
    private ExchangeService exchangeService;

    @GetMapping("/api/rates")
    @Secured("ROLE_USER")
    public List<CurrencyRateResponse> getRates() {
        return exchangeService.getRates();
    }
}
