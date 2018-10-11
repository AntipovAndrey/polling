package ru.andrey.poll.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.andrey.poll.payload.login.JwtAuthenticationResponse;
import ru.andrey.poll.payload.login.LoginRequest;
import ru.andrey.poll.payload.signup.ApiResponse;
import ru.andrey.poll.payload.signup.SignUpRequest;
import ru.andrey.poll.service.AuthenticationService;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthenticationResponse jwt = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        ApiResponse result = authenticationService.register(signUpRequest);
        if (!result.getSuccess()) {
            return ResponseEntity.badRequest().body(result);
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(signUpRequest.getUsername())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }
}
