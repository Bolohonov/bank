package org.example.transferservice.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

    @JsonProperty("email")
    private String email;

    @JsonProperty("message")
    private String message;

    @JsonProperty("application")
    private String application;

}
