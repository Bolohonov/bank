package org.example.exchangeservice.controller;

import org.example.exchangeservice.response.CurrencyRateResponse;
import org.example.exchangeservice.response.CurrencyResponse;
import org.example.exchangeservice.response.HttpResponseDto;
import org.example.exchangeservice.service.CurrencyService;
import org.example.exchangeservice.service.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {

    @Autowired
    private RatesService ratesService;

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("currency")
    public List<CurrencyResponse> getCurrency() {
        return currencyService.getCurrency();
    }

    @PostMapping("rates")
    @Secured("SCOPE_exchange.write")
    public HttpResponseDto saveRates(@RequestBody List<CurrencyRateResponse> currencyRateDtos) {
        ratesService.saveRates(currencyRateDtos);
        return HttpResponseDto.builder()
                .statusMessage("Курсы валют успешно сохранены.")
                .statusCode("0")
                .build();
    }

    @GetMapping("rates")
    @Secured("SCOPE_exchange.read")
    public List<CurrencyRateResponse> getAllRates() {
        return ratesService.getAllRates();
    }

    @ExceptionHandler
    public HttpResponseDto handleException(Exception ex) {
        ex.printStackTrace();
        return HttpResponseDto.builder()
                .statusMessage(ex.getLocalizedMessage())
                .statusCode("999")
                .build();
    }

}
