package org.example.exchangeservice.service;

import org.example.exchangeservice.mapping.RatesMapper;
import org.example.exchangeservice.model.Rate;
import org.example.exchangeservice.repository.RatesRepository;
import org.example.exchangeservice.response.CurrencyRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatesService {

    @Autowired
    private RatesMapper ratesMapper;

    @Autowired
    private RatesRepository ratesRepository;

    @Transactional
    public void saveRates(List<CurrencyRateResponse> currencyRateDtos) {
        List<Rate> rates = currencyRateDtos.stream()
                .map(ratesMapper::toEntity)
                .collect(Collectors.toList());
        ratesRepository.deleteAll();
        ratesRepository.saveAll(rates);
    }

    public List<CurrencyRateResponse> getAllRates() {
        List<Rate> rates = ratesRepository.findAll();

        return rates.stream()
                .map(ratesMapper::toDto)
                .collect(Collectors.toList());
    }
}
