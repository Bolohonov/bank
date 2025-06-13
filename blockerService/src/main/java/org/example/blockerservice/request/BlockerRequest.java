package org.example.blockerservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockerRequest {
    private String currency;
    private String login;
    private String action;
    private Double amount;
}
