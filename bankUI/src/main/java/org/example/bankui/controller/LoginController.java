package org.example.bankui.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bankui.response.HttpResponseDto;
import org.example.bankui.response.UserResponse;
import org.example.bankui.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutDefault(HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            SecurityContextHolder.clearContext();
        }

        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login?logout";
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(HttpServletRequest request, Model model) {
        // Получение CSRF-токена
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("_csrf", csrfToken);

        // Получение данных формы
        String confirmPassword = request.getParameter("confirm_password");
        String password = request.getParameter("password");
        String login = request.getParameter("login");
        String fio = request.getParameter("name");
        String emain = request.getParameter("email");
        String dateOfBirthString = request.getParameter("dateOfBirth");

        List<String> errors = new ArrayList<>();
        if ((confirmPassword != null) && (password != null) && (!confirmPassword.equals(password))) {
            errors.add("Пароли не совпадают");
        }

        if (!errors.isEmpty()) {
            attributes.put("errors", errors);
            model.addAllAttributes(attributes);
            return "signup";
        }

        StandardPasswordEncoder encoder = new StandardPasswordEncoder();
        UserResponse userDto = UserResponse.builder()
                .dateOfBirth(dateOfBirthString)
                .login(login)
                .password(encoder.encode(password))
                .email(emain)
                .fio(fio)
                .role("USER")
                .build();

        HttpResponseDto httpResponseDto = accountService.registerUser(userDto);

        if (!httpResponseDto.getStatusCode().equals("0")) {
            errors.add(httpResponseDto.getStatusMessage());
            attributes.put("errors", errors);
            model.addAllAttributes(attributes);
            return "signup";
        }

        return "redirect:/login?registeredSuccess";
    }
}
