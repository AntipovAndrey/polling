package ru.andrey.poll.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andrey.poll.exception.ResourceNotFoundException;
import ru.andrey.poll.model.User;
import ru.andrey.poll.payload.user.UserProfile;
import ru.andrey.poll.payload.user.UserSummary;
import ru.andrey.poll.repository.PollRepository;
import ru.andrey.poll.repository.UserRepository;
import ru.andrey.poll.repository.VoteRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    private void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    class EmailExistence {

        private String email;

        @BeforeEach
        private void setUp() throws Exception {
            email = "example@site.com";
        }

        @Test
        void whenUserExistsInRepositoryByEmailThenReturnExists() {
            when(userRepository.existsByEmail(email)).thenReturn(true);

            assertThat(userService.existsByEmail(email).isAvailable(), is(true));
        }

        @Test
        void whenUserNotExistsInRepositoryByEmailThenReturnNotExists() {
            when(userRepository.existsByEmail(email)).thenReturn(false);

            assertThat(userService.existsByEmail(email).isAvailable(), is(false));
        }
    }


    @Nested
    class UsernameExistence {

        private String username;

        @BeforeEach
        private void setUp() throws Exception {
            username = "username";
        }

        @Test
        void whenUserExistsInRepositoryByNameThenReturnExists() {
            when(userRepository.existsByUsername(username)).thenReturn(true);

            assertThat(userService.existsUsername(username).isAvailable(), is(true));
        }

        @Test
        void whenUserNotExistsInRepositoryByNameThenReturnNotExists() {
            when(userRepository.existsByUsername(username)).thenReturn(false);

            assertThat(userService.existsUsername(username).isAvailable(), is(false));
        }
    }

    @Nested
    class UserRetrieving {

        private User user;
        private String username;

        @BeforeEach
        private void setUp() throws Exception {
            username = "username";
            user = new User("name", username, "com@example.ru", "password") {{
                setId(1L);
            }};
        }

        @Test
        void whenUserNotFoundThenThrowException() {
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
            Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getPublicProfile(anyString()));
        }

        @Test
        void whenUserFoundThenFillAModel() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            UserSummary userSummary = userService.findByUsername(username);

            assertThat(userSummary.getId(), is(notNullValue()));
            assertThat(userSummary.getName(), equalTo(user.getName()));
            assertThat(userSummary.getUsername(), equalTo(username));
        }

        @Test
        void whenUserFoundThenReturnPublicProfile() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(pollRepository.countByCreatedBy(user.getId())).thenReturn(10L);
            when(voteRepository.countByUserId(user.getId())).thenReturn(20L);

            UserProfile userSummary = userService.getPublicProfile(username);

            assertThat(userSummary.getId(), is(notNullValue()));
            assertThat(userSummary.getName(), equalTo(user.getName()));
            assertThat(userSummary.getUsername(), equalTo(username));
            assertThat(userSummary.getPollCount(), equalTo(10L));
            assertThat(userSummary.getVoteCount(), equalTo(20L));
        }
    }
}
