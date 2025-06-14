package org.example.transferservice.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HttpResponseDto {
    private String statusCode;
    private String statusMessage;
}
