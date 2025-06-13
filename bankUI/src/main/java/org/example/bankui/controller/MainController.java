package org.example.bankui.controller;

import org.example.bankui.mapper.AccountMapper;
import org.example.bankui.mapper.BankUserMapper;
import org.example.bankui.mapper.CurrencyMapper;
import org.example.bankui.model.Account;
import org.example.bankui.model.BankUser;
import org.example.bankui.model.Currency;
import org.example.bankui.response.AccountResponse;
import org.example.bankui.response.CurrencyResponse;
import org.example.bankui.response.UserResponse;
import org.example.bankui.response.UserShortResponse;
import org.example.bankui.service.AccountService;
import org.example.bankui.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class MainController {

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private BankUserMapper userMapper;

    @GetMapping("/main")
    @Secured("ROLE_USER")
    public String mainPage(Model model) {
        UserResponse currentUser = getCurrentUser();

        model.addAttribute("login", currentUser.getLogin());
        model.addAttribute("name", currentUser.getFio());
        model.addAttribute("birthdate", currentUser.getDateOfBirth());

        List<CurrencyResponse> currencyDtos = exchangeService.getCurrency();
        List<Currency> currencyModels = currencyDtos.stream()
                .map(currencyMapper::toModel)
                .collect(Collectors.toList());
        model.addAttribute("currencies", currencyModels);

        List<AccountResponse> accountDtos = accountService.getUserAccountInfo(currentUser.getLogin());
        List<Account> accountModels = accountDtos.stream()
                .map(accountDto -> accountMapper.toModel(accountDto, currencyDtos))
                .collect(Collectors.toList());
        model.addAttribute("accounts", accountModels);

        List<UserShortResponse> userListResponseDtos = accountService.getAllUsers();
        List<BankUser> usersModels = userListResponseDtos.stream()
                .map(userMapper::toModel)
                .collect(Collectors.toList());
        model.addAttribute("users", usersModels);

        return "main";
    }


    private UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserResponse) {
            return (UserResponse) authentication.getPrincipal();
        }

        return UserResponse.builder()
                .login("guest")
                .password("password")
                .fio("Гость")
                .role("USER")
                .email("guest@example.com")
                .dateOfBirth("1990-01-01")
                .build();
    }
}
