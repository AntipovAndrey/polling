package ru.andrey.poll.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.andrey.poll.payload.PagedResponse;
import ru.andrey.poll.payload.poll.PollResponse;
import ru.andrey.poll.payload.user.UserIdentityAvailability;
import ru.andrey.poll.payload.user.UserProfile;
import ru.andrey.poll.payload.user.UserSummary;
import ru.andrey.poll.security.CurrentUser;
import ru.andrey.poll.security.UserPrincipal;
import ru.andrey.poll.service.PollService;
import ru.andrey.poll.service.UserService;
import ru.andrey.poll.util.Constants;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final PollService pollService;

    @Autowired
    public UserController(UserService userService, PollService pollService) {
        this.userService = userService;
        this.pollService = pollService;
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return UserSummary.builder()
                .id(currentUser.getId())
                .name(currentUser.getName())
                .username(currentUser.getUsername())
                .build();
    }

    @GetMapping("/api/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam("username") String username) {
        return userService.existsUsername(username);
    }

    @GetMapping("/api/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam("email") String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable("username") String username) {
        return userService.getPublicProfile(username);
    }

    @GetMapping("/users/{username}/polls")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable("username") String username,
                                                         @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(
                                                                 value = "page",
                                                                 defaultValue = Constants.DEFAULT_PAGE_NUMBER
                                                         ) int page,
                                                         @RequestParam(
                                                                 value = "size",
                                                                 defaultValue = Constants.DEFAULT_PAGE_SIZE
                                                         ) int size) {
        return pollService.getPollsCreatedBy(username, currentUser, page, size);
    }

    @GetMapping("/users/{username}/votes")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable("username") String username,
                                                       @CurrentUser UserPrincipal currentUser,
                                                       @RequestParam(
                                                               value = "page",
                                                               defaultValue = Constants.DEFAULT_PAGE_NUMBER
                                                       ) int page,
                                                       @RequestParam(
                                                               value = "size",
                                                               defaultValue = Constants.DEFAULT_PAGE_SIZE
                                                       ) int size) {
        return pollService.getPollsVotedBy(username, currentUser, page, size);
    }
}
