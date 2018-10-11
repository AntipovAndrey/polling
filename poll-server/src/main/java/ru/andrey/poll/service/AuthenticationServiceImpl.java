package ru.andrey.poll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.andrey.poll.exception.AppException;
import ru.andrey.poll.model.Role;
import ru.andrey.poll.model.RoleName;
import ru.andrey.poll.model.User;
import ru.andrey.poll.payload.login.JwtAuthenticationResponse;
import ru.andrey.poll.payload.login.LoginRequest;
import ru.andrey.poll.payload.signup.ApiResponse;
import ru.andrey.poll.payload.signup.SignUpRequest;
import ru.andrey.poll.repository.RoleRepository;
import ru.andrey.poll.repository.UserRepository;
import ru.andrey.poll.security.JwtTokenProvider;

import javax.validation.Valid;
import java.util.Collections;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public JwtAuthenticationResponse authenticate(@Valid LoginRequest user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsernameOrEmail(),
                        user.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return new JwtAuthenticationResponse(token);
    }

    @Override
    public ApiResponse register(@Valid SignUpRequest user) {
        if (existsUsername(user.getUsername())) {
            return ApiResponse.error("Username is already taken");
        }
        if (existsByEmail(user.getEmail())) {
            return ApiResponse.error("Email address already in use");
        }
        createUser(user);
        return ApiResponse.ok("User registered successfully");
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private void createUser(SignUpRequest request) {
        User user = new User(request.getName(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("Roles was not initialized"));
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
    }
}
