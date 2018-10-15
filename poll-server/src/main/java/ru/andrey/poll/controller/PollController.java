package ru.andrey.poll.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.andrey.poll.payload.PagedResponse;
import ru.andrey.poll.payload.poll.PollRequest;
import ru.andrey.poll.payload.poll.PollResponse;
import ru.andrey.poll.payload.poll.PollSummary;
import ru.andrey.poll.payload.signup.ApiResponse;
import ru.andrey.poll.payload.vote.VoteRequest;
import ru.andrey.poll.security.CurrentUser;
import ru.andrey.poll.security.UserPrincipal;
import ru.andrey.poll.service.PollService;
import ru.andrey.poll.util.Constants;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    private final PollService pollService;

    @Autowired
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping
    public PagedResponse<PollResponse> getPolls(@CurrentUser UserPrincipal currentUser,
                                                @RequestParam(
                                                        value = "next",
                                                        defaultValue = Constants.FIRST_PAGE_S_NEXT
                                                ) int page,
                                                @RequestParam(
                                                        value = "size",
                                                        defaultValue = Constants.DEFAULT_PAGE_SIZE
                                                ) int size) {
        return pollService.getAllPolls(currentUser, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest) {
        PollSummary response = pollService.createPoll(pollRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{pollId}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.ok("Poll Created Successfully"));
    }

    @GetMapping("/{pollId}")
    public PollResponse getPollById(@CurrentUser UserPrincipal currentUser,
                                    @PathVariable("pollId") Long pollId) {
        return pollService.getPollById(pollId, currentUser);
    }

    @PostMapping("/{pollId}/votes")
    @PreAuthorize("hasRole('USER')")
    public PollResponse castVote(@CurrentUser UserPrincipal userPrincipal,
                                 @PathVariable("pollId") Long pollId,
                                 @Valid @RequestBody VoteRequest voteRequest) {
        return pollService.vote(pollId, voteRequest, userPrincipal);
    }
}
