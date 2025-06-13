package org.example.bankui.mvctest;

import org.example.bankui.BankUiApplication;
import org.example.bankui.TestSecurityConfig;
import org.example.bankui.response.AccountResponse;
import org.example.bankui.response.CurrencyResponse;
import org.example.bankui.response.UserResponse;
import org.example.bankui.response.UserShortResponse;
import org.example.bankui.service.AccountService;
import org.example.bankui.service.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {BankUiApplication.class, TestSecurityConfig.class})
@TestPropertySource(locations = "classpath:application.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(reset = MockReset.BEFORE)
    private ExchangeService exchangeApplicationService;

    @MockitoBean(reset = MockReset.BEFORE)
    private AccountService accountApplicationService;

    @BeforeEach
    void setUp() {
        // Настройка SecurityContext для имитации аутентифицированного пользователя
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(
                UserResponse.builder()
                        .login("user1")
                        .fio("Иван Иванов")
                        .dateOfBirth("1990-01-01")
                        .build()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void testMainPage() throws Exception {
        // Arrange
        List<CurrencyResponse> currencyDtos = List.of(
                CurrencyResponse.builder().currency("USD").title("Доллар").build(),
                CurrencyResponse.builder().currency("EUR").title("Рубль").build()
        );

        List<AccountResponse> accountDtos = List.of(
                AccountResponse.builder().accountNumber("1234567890").currency("USD").balance(1000.0).build(),
                AccountResponse.builder().accountNumber("0987654321").currency("EUR").balance(500.0).build()
        );

        List<UserShortResponse> userListResponseDtos = List.of(
                UserShortResponse.builder().login("user1").fio("Иван Иванов").email("user1@example.com").build(),
                UserShortResponse.builder().login("user2").fio("Петр Петров").email("user2@example.com").build()
        );

        when(exchangeApplicationService.getCurrency()).thenReturn(currencyDtos);
        when(accountApplicationService.getUserAccountInfo("user1")).thenReturn(accountDtos);
        when(accountApplicationService.getAllUsers()).thenReturn(userListResponseDtos);

        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("login"))
                .andExpect(model().attributeExists("name"))
                .andExpect(model().attributeExists("birthdate"))
                .andExpect(model().attributeExists("currencies"))
                .andExpect(model().attributeExists("accounts"))
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("main"));

        verify(exchangeApplicationService, times(1)).getCurrency();
        verify(accountApplicationService, times(1)).getUserAccountInfo("guest");
        verify(accountApplicationService, times(1)).getAllUsers();
    }
}
