package org.example.exchangeservice.repository;

import org.example.exchangeservice.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatesRepository extends JpaRepository<Rate, Long> {
}
