package org.example.exchangeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rates", schema = "exchange")
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "buy", nullable = false)
    private BigDecimal buy;

    @Column(name = "sale", nullable = false)
    private BigDecimal sale;

}
