package org.example.transferservice.controller;

import org.example.transferservice.reponse.HttpResponseDto;
import org.example.transferservice.request.TransferOperationRequest;
import org.example.transferservice.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping
    @Secured("SCOPE_transfer.write")
    public HttpResponseDto transferOperation(@RequestBody TransferOperationRequest request) {
        transferService.transferOperation(request);
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
