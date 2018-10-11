package ru.andrey.poll.service;

import org.springframework.stereotype.Service;
import ru.andrey.poll.payload.login.JwtAuthenticationResponse;
import ru.andrey.poll.payload.login.LoginRequest;
import ru.andrey.poll.payload.signup.ApiResponse;
import ru.andrey.poll.payload.signup.SignUpRequest;

import javax.validation.Valid;

@Service
public interface AuthenticationService {

    JwtAuthenticationResponse authenticate(@Valid LoginRequest user);

    ApiResponse register(@Valid SignUpRequest user);
}
