package ru.andrey.poll.service;

import org.springframework.stereotype.Service;
import ru.andrey.poll.payload.user.UserIdentityAvailability;
import ru.andrey.poll.payload.user.UserProfile;
import ru.andrey.poll.payload.user.UserSummary;

import java.util.List;

@Service
public interface UserService {

    UserIdentityAvailability existsByEmail(String email);

    UserIdentityAvailability existsUsername(String username);

    UserProfile getPublicProfile(String username);

    UserSummary findByUsername(String username);

    List<UserSummary> findByIdIn(List<Long> ids);

    UserSummary findById(Long createdBy);
}
