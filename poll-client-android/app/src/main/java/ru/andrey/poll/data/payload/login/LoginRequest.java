package ru.andrey.poll.data.payload.login;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginRequest {

    private String usernameOrEmail;
    private String password;
}
