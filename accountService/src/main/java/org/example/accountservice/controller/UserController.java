package org.example.accountservice.controller;

import org.example.accountservice.request.UserRequest;
import org.example.accountservice.response.ChangePasswordRequest;
import org.example.accountservice.response.HttpResponseDto;
import org.example.accountservice.response.UserResponse;
import org.example.accountservice.response.UserShortResponse;
import org.example.accountservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("SCOPE_accounts.write")
    public HttpResponseDto createUser(@RequestBody UserRequest request) {
        Long userId = userService.createUser(request);
        return HttpResponseDto.builder()
                .statusMessage("Create User OK")
                .statusCode("0")
                .build();
    }

    @PostMapping("/changepassword")
    @Secured("SCOPE_accounts.write")
    @ResponseStatus(HttpStatus.OK)
    public HttpResponseDto chengePassword(@RequestBody ChangePasswordRequest request) {
        userService.chengePassword(request);
        return HttpResponseDto.builder()
                .statusMessage("Chenge Password User OK")
                .statusCode("0")
                .build();
    }

    @PostMapping("/edit")
    @Secured("SCOPE_accounts.write")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpResponseDto editUser(@RequestBody UserRequest request) {
        Long userId = userService.editUser(request);
        return HttpResponseDto.builder()
                .statusMessage("Edit User OK")
                .statusCode("0")
                .build();
    }

    @GetMapping("/findbylogin")
    @Secured("SCOPE_accounts.read")
    public UserResponse findByUsername(@RequestParam("login") String login) {
        UserResponse userResponseDto = userService.findByUsername(login);
        if (userResponseDto == null) {
            userResponseDto = UserResponse.builder()
                    .statusCode("999")
                    .statusMessage("Пользователь не найден")
                    .build();
        } else  {
            userResponseDto.setStatusCode("0");
            userResponseDto.setStatusMessage("OK");
        }
        return userResponseDto;
    }

    @GetMapping("/all")
    @Secured("SCOPE_accounts.read")
    public List<UserShortResponse> getUsers() {
        return userService.findAll();
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
