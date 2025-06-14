package org.example.transferservice.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyRateResponse {
    private String currency;
    private Double buy;
    private Double sale;

}
