package ru.andrey.poll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.andrey.poll.exception.ResourceNotFoundException;
import ru.andrey.poll.model.User;
import ru.andrey.poll.payload.user.UserIdentityExistence;
import ru.andrey.poll.payload.user.UserProfile;
import ru.andrey.poll.payload.user.UserSummary;
import ru.andrey.poll.repository.PollRepository;
import ru.andrey.poll.repository.UserRepository;
import ru.andrey.poll.repository.VoteRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PollRepository pollRepository,
                           VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public UserIdentityExistence existsByEmail(String email) {
        return UserIdentityExistence.of(userRepository.existsByEmail(email));
    }

    @Override
    public UserIdentityExistence existsUsername(String username) {
        return UserIdentityExistence.of(userRepository.existsByUsername(username));
    }

    @Override
    public UserProfile getPublicProfile(String username) {
        User user = getUser(username);
        return UserProfile.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .joinedAt(user.getCreatedAt())
                .pollCount(pollRepository.countByCreatedBy(user.getId()))
                .voteCount(voteRepository.countByUserId(user.getId()))
                .build();
    }

    @Override
    public UserSummary findByUsername(String username) {
        return toSummary(getUser(username));
    }

    @Override
    public List<UserSummary> findByIdIn(List<Long> ids) {
        return userRepository.findByIdIn(ids)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public UserSummary findById(Long createdBy) {
        return toSummary(getUser(createdBy));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Objects.toString(id)));
    }

    private UserSummary toSummary(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }


    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
}
