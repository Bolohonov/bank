package org.example.bankui.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency {
    private String currencyCode;
    private String title;

}
