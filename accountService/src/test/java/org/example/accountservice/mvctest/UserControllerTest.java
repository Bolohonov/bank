package org.example.accountservice.mvctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.accountservice.AccountServiceApplication;
import org.example.accountservice.TestSecurityConfig;
import org.example.accountservice.request.UserRequest;
import org.example.accountservice.response.ChangePasswordRequest;
import org.example.accountservice.response.UserResponse;
import org.example.accountservice.response.UserShortResponse;
import org.example.accountservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {AccountServiceApplication.class, TestSecurityConfig.class})
@TestPropertySource(locations = "classpath:application.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean(reset = MockReset.BEFORE)
    private UserService userService;


    @Test
    @WithMockUser(authorities = "SCOPE_accounts.write")
    void testCreateUser() throws Exception {
        UserRequest request = new UserRequest(
                "user1", "password1", "Иван Иванов", "USER", "user1@example.com", "1980-01-01"
        );
        when(userService.createUser(request)).thenReturn(1L);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMessage").value("Create User OK"))
                .andExpect(jsonPath("$.statusCode").value("0"));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_accounts.write")
    void testChangePassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("user1", "newPassword123");
        doNothing().when(userService).chengePassword(request);

        mockMvc.perform(post("/user/changepassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value("Chenge Password User OK"))
                .andExpect(jsonPath("$.statusCode").value("0"));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_accounts.read")
    void testFindByUsername() throws Exception {
        String login = "user1";
        UserResponse mockUser = new UserResponse(
                "user1", "password123", "Иван Иванов", "USER", "user1@example.com", "1990-01-01", "0", "OK"
        );
        when(userService.findByUsername(login)).thenReturn(mockUser);

        mockMvc.perform(get("/user/findbylogin")
                        .param("login", login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user1"))
                .andExpect(jsonPath("$.fio").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("user1@example.com"))
                .andExpect(jsonPath("$.statusCode").value("0"))
                .andExpect(jsonPath("$.statusMessage").value("OK"));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_accounts.read")
    void testGetUsers() throws Exception {

        List<UserShortResponse> mockUsers = List.of(
                new UserShortResponse("user1", "Иван Иванов", "user1@example.com", "1980-01-01"),
                new UserShortResponse("user2", "Петр Петров", "user2@example.com", "1995-05-05")
        );
        when(userService.findAll()).thenReturn(mockUsers);


        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("user1"))
                .andExpect(jsonPath("$[0].fio").value("Иван Иванов"))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].login").value("user2"))
                .andExpect(jsonPath("$[1].fio").value("Петр Петров"));
    }

}
