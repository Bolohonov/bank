package org.example.cashservice.controller;

import org.example.cashservice.dto.AccountOperationDto;
import org.example.cashservice.dto.HttpResponseDto;
import org.example.cashservice.service.CashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cash")
public class CashController {

    @Autowired
    private CashService cashService;

    @PostMapping("/in")
    @Secured("SCOPE_cash.write")
    public HttpResponseDto cashInOperation(@RequestBody AccountOperationDto accountOperationDto) {
        cashService.cashIn(accountOperationDto);
        return HttpResponseDto.builder()
                .statusMessage("Операция выполнена успешно")
                .statusCode("0")
                .build();
    }

    @PostMapping("/out")
    @Secured("SCOPE_cash.write")
    public HttpResponseDto cashOutOperation(@RequestBody AccountOperationDto accountOperationDto) {
        cashService.cashOut(accountOperationDto);
        return HttpResponseDto.builder()
                .statusMessage("Операция выполнена успешно")
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
