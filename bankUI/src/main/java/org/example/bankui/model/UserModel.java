package org.example.bankui.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserModel {
    private String login;
    private String name;
}
