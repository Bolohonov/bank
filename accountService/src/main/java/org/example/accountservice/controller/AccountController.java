package org.example.accountservice.controller;

import org.example.accountservice.request.AccountRequest;
import org.example.accountservice.response.AccountOperation;
import org.example.accountservice.response.AccountResponse;
import org.example.accountservice.response.HttpResponseDto;
import org.example.accountservice.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountsService accountsService;

    @ExceptionHandler
    public HttpResponseDto handleException(Exception ex) {
        ex.printStackTrace();
        return HttpResponseDto.builder()
                .statusMessage(ex.getLocalizedMessage())
                .statusCode("999")
                .build();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("SCOPE_accounts.write")
    public HttpResponseDto createAccount(@RequestBody AccountRequest request) {
        Long accountId = accountsService.addAccount(request);
        return HttpResponseDto.builder()
                .statusMessage("Create Account User OK")
                .statusCode("0")
                .build();
    }

    @PutMapping("/cashIn")
    @Secured("SCOPE_accounts.write")
    public HttpResponseDto cashInOperation(@RequestBody AccountOperation request) {
        accountsService.cashIn(request);
        return HttpResponseDto.builder()
                .statusMessage("Операция выполнена успешно")
                .statusCode("0")
                .build();
    }

    @PutMapping("/cashOut")
    @Secured("SCOPE_accounts.write")
    public HttpResponseDto cashOutOperation(@RequestBody AccountOperation request) {
        accountsService.cashOut(request);
        return HttpResponseDto.builder()
                .statusMessage("Операция выполнена успешно")
                .statusCode("0")
                .build();
    }

    @GetMapping("/all")
    @Secured("SCOPE_accounts.read")
    public List<AccountResponse> getAccountsByUsername(@RequestParam("login") String login) {
        return accountsService.findAccountsByLogin(login);
    }

    @GetMapping("/get")
    @Secured("SCOPE_accounts.read")
    public AccountResponse getAccountInfo(@RequestParam("currency") String currency, @RequestParam("login") String login) {
        return accountsService.findAccountByLoginAndCurrency(login, currency);
    }

}
