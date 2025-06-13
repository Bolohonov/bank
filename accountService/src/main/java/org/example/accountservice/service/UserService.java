package org.example.accountservice.service;

import org.example.accountservice.mapper.UserMapper;
import org.example.accountservice.model.User;
import org.example.accountservice.repository.UsersRepository;
import org.example.accountservice.request.UserRequest;
import org.example.accountservice.response.ChangePasswordRequest;
import org.example.accountservice.response.UserResponse;
import org.example.accountservice.response.UserShortResponse;
import org.example.accountservice.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    public UserResponse findByUsername(String login) {
        return usersRepository.findUserByLogin(login)
                .map(userMapper::toDto)
                .orElse(null);
    }

    public List<UserShortResponse> findAll() {
        return usersRepository.findAll().stream()
                .map(userMapper::toExternalDto)
                .collect(Collectors.toList());
    }

    public Long createUser(UserRequest request) {
        validateUser(request);

        var foundUser = usersRepository.findUserByLogin(request.getLogin());
        if (foundUser.isPresent()) {
            throw new RuntimeException("Пользователь с логином " + request.getLogin() + " уже зарегистрирован");
        }
        User user = userMapper.toEntity(request);
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("Не передан пароль");
        }
        user.setDatetimeCreate(LocalDateTime.now());
        User savedUser = usersRepository.save(user);
        Long userId=savedUser.getId();
        notificationService.sendNotification(request.getEmail(),"Новый пользователь успешно зарегистрирован");
        return userId;
    }

    public void chengePassword(ChangePasswordRequest changePasswordRequestDto) {

        var foundUser = usersRepository.findUserByLogin(changePasswordRequestDto.getLogin());
        if (!foundUser.isPresent()) {
            throw new RuntimeException("Пользователь с логином " + changePasswordRequestDto.getLogin() + " не зарегистрирован");
        }
        User user = foundUser.get();
        user.setPassword(changePasswordRequestDto.getPassword());
        usersRepository.save(user);
        notificationService.sendNotification(user.getEmail(),"Пароль пользователя успешно изменен");
    }

    public Long editUser(UserRequest userRequest) {
        validateUser(userRequest);

        var foundUser = usersRepository.findUserByLogin(userRequest.getLogin());
        if (!foundUser.isPresent()) {
            throw new RuntimeException("Пользователь с логином " + userRequest.getLogin() + " не зарегистрирован");
        }
        User user = foundUser.get();
        user.setFio(userRequest.getFio());
        user.setDateOfBirth(LocalDate.parse(userRequest.getDateOfBirth(), DateTimeUtils.DATE_TIME_FORMATTER));
        User savedUser = usersRepository.save(user);
        notificationService.sendNotification(user.getEmail(),"Пользователь успешно отредактирован");
        return savedUser.getId();
    }


    private void validateUser(UserRequest userRequest) {
        Optional.ofNullable(userRequest.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("Не передан логин"));

        Optional.ofNullable(userRequest.getFio())
                .orElseThrow(() -> new IllegalArgumentException("Не передано ФИО"));

        Optional.ofNullable(userRequest.getDateOfBirth())
                .orElseThrow(() -> new IllegalArgumentException("Не передана дата рождения клиента"));

        LocalDate dateOfBirth = null;
        try {
            dateOfBirth = LocalDate.parse(userRequest.getDateOfBirth(), DateTimeUtils.DATE_TIME_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Использован некорректный формат даты. Корректный формат - yyyy-MM-dd");
        }
        if (!DateTimeUtils.isAdult(dateOfBirth)) {
            throw new IllegalArgumentException("Клиент должен быть старше 18 лет");
        }
    }


}
