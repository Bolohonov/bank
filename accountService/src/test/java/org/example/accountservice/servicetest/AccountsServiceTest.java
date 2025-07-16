package org.example.accountservice.servicetest;

import org.example.accountservice.AccountServiceApplication;
import org.example.accountservice.TestSecurityConfig;
import org.example.accountservice.model.Account;
import org.example.accountservice.model.User;
import org.example.accountservice.repository.AccountsRepository;
import org.example.accountservice.repository.UsersRepository;
import org.example.accountservice.request.AccountRequest;
import org.example.accountservice.response.AccountOperation;
import org.example.accountservice.response.AccountResponse;
import org.example.accountservice.response.HttpResponseDto;
import org.example.accountservice.service.AccountsService;
import org.example.accountservice.service.NotificationProducer;
import org.example.accountservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AccountServiceApplication.class, TestSecurityConfig.class})
@TestPropertySource(locations = "classpath:application.yml")
@ActiveProfiles("test")
public class AccountsServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    private NotificationProducer notificationProducer;

    private User user;

    @BeforeEach
    void setUp() {
        accountsRepository.deleteAll();
        usersRepository.deleteAll();

        user = new User();
        user.setLogin("user1");
        user.setPassword("password123");
        user.setFio("Иван Иванов");
        user.setEmail("user1@example.com");
        user.setRole("USER");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setDatetimeCreate(LocalDateTime.now());
        usersRepository.save(user);
    }

    @Test
    void testAddAccount() {
        AccountRequest request = new AccountRequest("USD", "user1");

        HttpResponseDto mockResponse = HttpResponseDto.builder()
                .statusCode("0")
                .statusMessage("OK")
                .build();

        when(notificationProducer.sendNotification(anyString(),anyString())).thenReturn(mockResponse);
        Long accountId = accountsService.addAccount(request);

        assertNotNull(accountId);
        List<Account> accounts = accountsRepository.findAllByUser(user);
        assertEquals(1, accounts.size());
        assertEquals("USD", accounts.get(0).getCurrency());
        assertEquals(0, accounts.get(0).getBalance().doubleValue());

        verify(notificationProducer, times(1)).sendNotification(anyString(),anyString());
    }

    @Test
    void testCashIn() {
        Account account = new Account();
        account.setUser(user);
        account.setCurrency("USD");
        account.setNumber("40215USD123456789012");
        account.setBalance(BigDecimal.ZERO);
        account.setDatetimeCreate(LocalDateTime.now());
        accountsRepository.save(account);

        AccountOperation operation = new AccountOperation("USD", "user1", 100.0);

        accountsService.cashIn(operation);

        List<Account> accounts = accountsRepository.findAllByUser(user);
        assertEquals(1, accounts.size());
        assertEquals(100, accounts.get(0).getBalance().doubleValue());
    }

    @Test
    void testCashOutFalure() {
        Account account = new Account();
        account.setUser(user);
        account.setCurrency("USD");
        account.setNumber("40215USD123456789012");
        account.setBalance(BigDecimal.valueOf(50.0));
        account.setDatetimeCreate(LocalDateTime.now());
        accountsRepository.save(account);

        AccountOperation operation = new AccountOperation("USD", "user1", 100.0);

        Exception exception = assertThrows(RuntimeException.class, () -> accountsService.cashOut(operation));
        assertEquals("У пользователя user1 не достаточно денег на счету", exception.getMessage());
    }

    @Test
    void testFindAccountsByUsername() {
        Account account1 = new Account();
        account1.setUser(user);
        account1.setCurrency("USD");
        account1.setNumber("40215USD123456789012");
        account1.setBalance(BigDecimal.ZERO);
        account1.setDatetimeCreate(LocalDateTime.now());

        Account account2 = new Account();
        account2.setUser(user);
        account2.setCurrency("EUR");
        account2.setNumber("40215EUR123456789012");
        account2.setBalance(BigDecimal.ZERO);
        account2.setDatetimeCreate(LocalDateTime.now());

        accountsRepository.saveAll(List.of(account1, account2));

        List<AccountResponse> accounts = accountsService.findAccountsByLogin("user1");

        assertEquals(2, accounts.size());
        assertEquals("USD", accounts.get(0).getCurrency());
        assertEquals("EUR", accounts.get(1).getCurrency());
    }
}
