package org.example.blockerservice.controller;

import org.example.blockerservice.request.BlockerRequest;
import org.example.blockerservice.response.HttpResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/blocker")
public class BlockerController {

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Secured("SCOPE_blocker.read")
    public HttpResponseDto checkBlocker(@RequestBody BlockerRequest request) {
        Random random = new Random();
        if ((random.nextInt(100) > 90) && (!request.getCurrency().equals("XXX"))) {
            return HttpResponseDto.builder()
                    .statusMessage("Operation denied")
                    .statusCode("998")
                    .build();
        }
        return HttpResponseDto.builder()
                .statusMessage("Operation access")
                .statusCode("0")
                .build();
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
