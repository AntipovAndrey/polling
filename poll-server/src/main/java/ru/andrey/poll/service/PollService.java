package ru.andrey.poll.service;

import org.springframework.stereotype.Service;
import ru.andrey.poll.payload.PagedResponse;
import ru.andrey.poll.payload.poll.PollRequest;
import ru.andrey.poll.payload.poll.PollResponse;
import ru.andrey.poll.payload.poll.PollSummary;
import ru.andrey.poll.payload.vote.VoteRequest;
import ru.andrey.poll.security.UserPrincipal;

@Service
public interface PollService {

    PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size);

    PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int page, int size);

    PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size);

    PollResponse getPollById(Long pollId, UserPrincipal currentUser);

    PollResponse vote(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser);

    PollSummary createPoll(PollRequest pollRequest);
}
