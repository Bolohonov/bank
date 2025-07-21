package org.example.bankui.controller;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.example.bankui.request.AccountOperation;
import org.example.bankui.request.AccountRequest;
import org.example.bankui.request.ChangePasswordRequest;
import org.example.bankui.request.TransferOperation;
import org.example.bankui.response.HttpResponseDto;
import org.example.bankui.response.UserResponse;
import org.example.bankui.service.AccountService;
import org.example.bankui.service.CashService;
import org.example.bankui.service.TransferService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final AccountService accountService;
    private final CashService cashService;
    private final TransferService transferService;
    private final MainController mainController;
    private final MeterRegistry meterRegistry;

    @Value("${metricsEnabled:true}")
    private boolean metricsEnabled;

    @PostMapping("/{login}/editPassword")
    @Secured("ROLE_USER")
    public String editPassword(
            @PathVariable String login,
            @RequestParam String password,
            @RequestParam String confirm_password,
            Model model,
            Principal principal) {

        if (!principal.getName().equals(login)) {
            return "redirect:/logout";
        }

        if (!password.equals(confirm_password)) {
            model.addAttribute("passwordErrors", List.of("Пароли не совпадают"));
            return "main";
        }

        StandardPasswordEncoder encoder = new StandardPasswordEncoder();
        ChangePasswordRequest changePasswordRequestDto = ChangePasswordRequest.builder()
                .login(login)
                .password(encoder.encode(password))
                .build();

        HttpResponseDto httpResponseDto = accountService.changePassword(changePasswordRequestDto);
        if (httpResponseDto.getStatusCode().equals("0")) {
            model.addAttribute("changedPassword", true);
        } else {
            model.addAttribute("passwordErrors",
                    List.of("Ошибка при изменении пароля" + httpResponseDto.getStatusMessage()));
        }

        return mainController.mainPage(model);
    }

    @PostMapping("/{login}/changeInfo")
    @Secured("ROLE_USER")
    public String changeInfo(
            @PathVariable String login,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String birthDate,
            Model model,
            Principal principal) {

        if (!principal.getName().equals(login)) {
            return "redirect:/logout";
        }

        UserResponse userDto = getCurrentUser();
        if (userDto == null) {
            return "redirect:/logout";
        }

        UserResponse changeUser = UserResponse.builder()
                .fio(name)
                .dateOfBirth(birthDate)
                .login(userDto.getLogin())
                .email(userDto.getEmail())
                .build();

        HttpResponseDto httpResponseDto = accountService.changeInfo(changeUser);
        if (httpResponseDto.getStatusCode().equals("0")) {
            model.addAttribute("userIsUpdated", true);
            userDto.setFio(name);
            userDto.setDateOfBirth(birthDate);
        } else {
            model.addAttribute("userAccountsErrors",
                    List.of("Ошибка при обновлении данных " + httpResponseDto.getStatusMessage()));
        }
        return mainController.mainPage(model);
    }


    private UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserResponse) {
            return (UserResponse) authentication.getPrincipal();
        }

        return null;
    }

    @PostMapping("/{login}/accounts")
    @Secured("ROLE_USER")
    public String openAccount(
            @PathVariable String login,
            @RequestParam String currency,
            Model model,
            Principal principal) {

        AccountRequest requestDto = new AccountRequest();
        requestDto.setCurrency(currency);
        requestDto.setLogin(login);

        HttpResponseDto httpResponseDto = accountService.accountAdd(requestDto);

        if (httpResponseDto.getStatusCode().equals("0")) {
            model.addAttribute("accountIsOpen", true);
        } else {
            model.addAttribute("accountErrors",
                    List.of("Ошибка при создании счета " + httpResponseDto.getStatusMessage()));
        }
        return mainController.mainPage(model);
    }


    @PostMapping("/{login}/cash")
    @Secured("ROLE_USER")
    public String handleCashOperation(
            @PathVariable String login,
            @RequestParam String currency,
            @RequestParam double value,
            @RequestParam String action,
            Model model) {

        AccountOperation accountOperationDto = AccountOperation.builder()
                .login(login)
                .currency(currency)
                .amount(value)
                .build();

        HttpResponseDto httpResponseDto;
        if ("PUT".equalsIgnoreCase(action)) {
            httpResponseDto = cashService.cashIn(accountOperationDto);
        } else if ("GET".equalsIgnoreCase(action)) {
            httpResponseDto = cashService.cashOut(accountOperationDto);
        } else {
            model.addAttribute("cashErrors", "Неверное действие: " + action);
            return mainController.mainPage(model);
        }

        if (httpResponseDto.getStatusCode().equals("0")) {
            model.addAttribute("cashIsOK", true);
        } else {
            model.addAttribute("cashErrors",
                    List.of("Ошибка операции " + httpResponseDto.getStatusMessage()));
        }
        return mainController.mainPage(model);
    }

    @PostMapping("/{login}/transfer")
    @Secured("ROLE_USER")
    public String handleTransferOperation(
            @PathVariable String login,
            @RequestParam(name = "from_currency" ) String fromCurrency,
            @RequestParam(name = "to_currency") String toCurrency,
            @RequestParam(name = "to_login") String toLogin,
            @RequestParam double value,
            Model model) {
        // Подготовка DTO для перевода
        TransferOperation transferOperationDto = TransferOperation.builder()
                .fromLogin(login)
                .toLogin(toLogin)
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .amount(value)
                .build();

        HttpResponseDto httpResponseDto = transferService.transfer(transferOperationDto);

        if ("0".equals(httpResponseDto.getStatusCode())) {
            if (transferOperationDto.getToLogin().equals(transferOperationDto.getFromLogin())) {
                model.addAttribute("transferIsOK", true);
            } else {
                model.addAttribute("transferOtherIsOK", true);
            }
        } else {
            if (metricsEnabled) {
                meterRegistry.counter("bank_operation_transfer_errors_total",
                                "from_login", transferOperationDto.getFromLogin(),
                                "to_login", transferOperationDto.getToLogin(),
                                "from_currency", transferOperationDto.getFromCurrency(),
                                "to_currency", transferOperationDto.getToCurrency(),
                                "type", transferOperationDto.getToLogin().equals(transferOperationDto.getFromLogin()) ? "own" : "external",
                                "reason", httpResponseDto.getStatusMessage())
                        .increment();
            }

            if (transferOperationDto.getToLogin().equals(transferOperationDto.getFromLogin())) {
                model.addAttribute("transferErrors",
                        List.of("Ошибка операции: " + httpResponseDto.getStatusMessage()));
            } else {
                model.addAttribute("transferOtherErrors",
                        List.of("Ошибка операции: " + httpResponseDto.getStatusMessage()));
            }
        }

        return mainController.mainPage(model);
    }
}
