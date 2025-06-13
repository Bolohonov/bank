package org.example.accountservice.response;

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
