package ru.andrey.poll.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andrey.poll.model.Role;
import ru.andrey.poll.model.RoleName;
import ru.andrey.poll.model.User;
import ru.andrey.poll.payload.signup.SignUpRequest;
import ru.andrey.poll.payload.user.UserIdentityExistence;
import ru.andrey.poll.repository.RoleRepository;
import ru.andrey.poll.repository.UserRepository;
import ru.andrey.poll.security.JwtTokenProvider;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    private void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    class TestRegister {

        private UserIdentityExistence exists;
        private UserIdentityExistence notExists;
        private SignUpRequest signUpRequest;

        private Role userRole;

        @BeforeEach
        private void setUp() throws Exception {
            exists = UserIdentityExistence.of(true);
            notExists = UserIdentityExistence.of(false);

            userRole = new Role() {{
                setName(RoleName.ROLE_USER);
            }};

            signUpRequest = new SignUpRequest() {{
                setName("name");
                setUsername("username");
                setEmail("email");
                setPassword("password");
            }};

            when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        }

        @Nested
        class UserNotExists {

            @BeforeEach
            private void setUp() throws Exception {
                when(userService.existsUsername(signUpRequest.getUsername())).thenReturn(notExists);
                when(userService.existsByEmail(signUpRequest.getEmail())).thenReturn(notExists);
            }

            @Test
            void whenUserNotExistsThenCreateUser() {
                assertThat(authenticationService.register(signUpRequest).getSuccess(), is(true));

                verify(passwordEncoder).encode(signUpRequest.getPassword());
                ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository).save(argumentCaptor.capture());
                assertThat(argumentCaptor.getValue().getName(), equalTo(signUpRequest.getName()));
                assertThat(argumentCaptor.getValue().getUsername(), equalTo(signUpRequest.getUsername()));
                assertThat(argumentCaptor.getValue().getEmail(), equalTo(signUpRequest.getEmail()));
            }
        }

        @Nested
        class UserExists {

            @Test
            void whenUserExistsByUsernameThenAbort() {
                when(userService.existsUsername(signUpRequest.getUsername())).thenReturn(exists);
                when(userService.existsByEmail(signUpRequest.getEmail())).thenReturn(notExists);

                assertThat(authenticationService.register(signUpRequest).getSuccess(), is(false));

                verify(userRepository, never()).save(any());
            }

            @Test
            void whenUserExistsByEmailThenAbort() {
                when(userService.existsUsername(signUpRequest.getUsername())).thenReturn(notExists);
                when(userService.existsByEmail(signUpRequest.getEmail())).thenReturn(exists);

                assertThat(authenticationService.register(signUpRequest).getSuccess(), is(false));

                verify(userRepository, never()).save(any());
            }
        }
    }
}
