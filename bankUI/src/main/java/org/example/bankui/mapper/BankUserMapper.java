package org.example.bankui.mapper;

import org.example.bankui.model.BankUser;
import org.example.bankui.response.UserShortResponse;
import org.springframework.stereotype.Component;

@Component
public class BankUserMapper {
    public BankUser toModel(UserShortResponse userShortResponse) {
        if (userShortResponse == null) {
            return null;
        }

        return BankUser.builder()
                .name(userShortResponse.getFio())
                .login(userShortResponse.getLogin())
                .build();
    }
}
