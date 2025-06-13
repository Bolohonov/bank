package org.example.accountservice.mapper;

import org.example.accountservice.model.User;
import org.example.accountservice.request.UserRequest;
import org.example.accountservice.response.UserResponse;
import org.example.accountservice.response.UserShortResponse;
import org.example.accountservice.utils.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserMapper {

    public UserResponse toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .login(user.getLogin())
                .role(user.getRole())
                .fio(user.getFio())
                .email(user.getEmail())
                .password(user.getPassword())
                .dateOfBirth(user.getDateOfBirth().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }

    public UserShortResponse toExternalDto(User user) {
        if (user == null) {
            return null;
        }

        return UserShortResponse.builder()
                .login(user.getLogin())
                .fio(user.getFio())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }



    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .login(request.getLogin())
                .fio(request.getFio())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth(),DateTimeUtils.DATE_TIME_FORMATTER))
                .password(request.getPassword())
                .email(request.getEmail())
                .role(request.getRole())
                .build();
    }


}
