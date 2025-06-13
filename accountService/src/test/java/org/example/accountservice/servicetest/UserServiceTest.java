package org.example.accountservice.servicetest;

import org.example.accountservice.AccountServiceApplication;
import org.example.accountservice.TestSecurityConfig;
import org.example.accountservice.model.User;
import org.example.accountservice.repository.AccountsRepository;
import org.example.accountservice.repository.UsersRepository;
import org.example.accountservice.request.UserRequest;
import org.example.accountservice.response.HttpResponseDto;
import org.example.accountservice.response.UserResponse;
import org.example.accountservice.service.NotificationService;
import org.example.accountservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AccountServiceApplication.class, TestSecurityConfig.class})
@TestPropertySource(locations = "classpath:application.yml")
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    private NotificationService notificationService;

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
    void testCreateUser() {
        UserRequest request = new UserRequest(
                "user2", "password456", "Петр Петров", "USER", "user2@example.com", "1985-05-05"
        );

        HttpResponseDto mockResponse = HttpResponseDto.builder()
                .statusCode("0")
                .statusMessage("OK")
                .build();

        when(notificationService.sendNotification(anyString(),anyString())).thenReturn(mockResponse);
        Long userId = userService.createUser(request);

        assertNotNull(userId);
        List<User> users = usersRepository.findAll();
        assertEquals(2, users.size());
        assertEquals("user2", users.get(1).getLogin());
        verify(notificationService, times(1)).sendNotification(anyString(),anyString());
    }

    @Test
    void testFindByUsername() {
        UserResponse response = userService.findByUsername("user1");

        assertNotNull(response);
        assertEquals("user1", response.getLogin());
        assertEquals("Иван Иванов", response.getFio());
    }

    @Test
    void testEditUser() {
        UserRequest request = new UserRequest(
                "user1", "newPassword123", "Иван Иванов (обновленный)", "USER", "user1@example.com", "1995-01-01"
        );

        HttpResponseDto mockResponse = HttpResponseDto.builder()
                .statusCode("0")
                .statusMessage("OK")
                .build();

        when(notificationService.sendNotification(anyString(),anyString())).thenReturn(mockResponse);
        Long userId = userService.editUser(request);


        assertNotNull(userId);
        User updatedUser = usersRepository.findUserByLogin("user1").orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Иван Иванов (обновленный)", updatedUser.getFio());
        assertEquals(LocalDate.of(1995, 1, 1), updatedUser.getDateOfBirth());
        verify(notificationService, times(1)).sendNotification(anyString(),anyString());
    }
}
