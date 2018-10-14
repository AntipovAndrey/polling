package ru.andrey.poll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.andrey.poll.exception.BadRequestException;
import ru.andrey.poll.exception.ResourceNotFoundException;
import ru.andrey.poll.model.*;
import ru.andrey.poll.payload.PagedResponse;
import ru.andrey.poll.payload.poll.PollRequest;
import ru.andrey.poll.payload.poll.PollResponse;
import ru.andrey.poll.payload.poll.PollSummary;
import ru.andrey.poll.payload.user.UserSummary;
import ru.andrey.poll.payload.vote.VoteRequest;
import ru.andrey.poll.repository.PollRepository;
import ru.andrey.poll.repository.UserRepository;
import ru.andrey.poll.repository.VoteRepository;
import ru.andrey.poll.security.UserPrincipal;
import ru.andrey.poll.util.Constants;
import ru.andrey.poll.util.ModelMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PollServiceImpl(PollRepository pollRepository,
                           VoteRepository voteRepository,
                           UserService userService,
                           UserRepository userRepository,
                           ModelMapper modelMapper) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);
        Pageable pageable = pageableByCreatedAt(page, size);
        Page<Poll> polls;
        if (page == 0) {
            polls = pollRepository.findAll(pageableByCreatedAt(0, size));
        } else {
            polls = pollRepository.findAllByIdLessThan((long) page, pageableByCreatedAt(0, size));
        }

        System.out.println(pageable);

        if (polls.getNumberOfElements() == 0) {
            return pollsWithContent(polls, Collections.emptyList());
        }

        List<Long> pollIds = polls.map(Poll::getId).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
        Map<Long, UserSummary> creatorMap = getPollCreatorMap(polls.getContent());

        List<PollResponse> pollResponses =
                polls.map(poll -> modelMapper.pollToPollResponse(
                        poll,
                        creatorMap.get(poll.getCreatedBy()),
                        pollUserVoteMap.getOrDefault(poll.getId(), null),
                        choiceVoteCountMap)).getContent();

        return pollsWithContent(polls, pollResponses);
    }

    @Override
    public PagedResponse<PollResponse> getPollsVotedBy(String username,
                                                       UserPrincipal currentUser,
                                                       int page, int size) {
        validatePageNumberAndSize(page, size);

        UserSummary user = userService.findByUsername(username);
        Pageable pageable = pageableByCreatedAt(page, size);
        Page<Long> userVotedPollIds = voteRepository.findVotedPollIdsByUserId(user.getId(), pageable);

        if (userVotedPollIds.getNumberOfElements() == 0) {
            return idsWithContent(userVotedPollIds, Collections.emptyList());
        }

        List<Long> pollIds = userVotedPollIds.getContent();

        Sort sort = createdAtSort();
        List<Poll> polls = pollRepository.findByIdIn(pollIds, sort);

        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
        Map<Long, UserSummary> creatorMap = getPollCreatorMap(polls);

        List<PollResponse> pollResponses = polls.stream().map(poll -> modelMapper.pollToPollResponse(poll,
                creatorMap.get(poll.getCreatedBy()),
                pollUserVoteMap.getOrDefault(poll.getId(), null),
                choiceVoteCountMap)).collect(Collectors.toList());

        return idsWithContent(userVotedPollIds, pollResponses);
    }

    @Override
    public PagedResponse<PollResponse> getPollsCreatedBy(String username,
                                                         UserPrincipal currentUser,
                                                         int page, int size) {
        validatePageNumberAndSize(page, size);
        UserSummary user = userService.findByUsername(username);

        Pageable pageable = pageableByCreatedAt(page, size);
        Page<Poll> polls = pollRepository.findByCreatedBy(user.getId(), pageable);

        if (polls.getNumberOfElements() == 0) {
            return pollsWithContent(polls, Collections.emptyList());
        }

        List<Long> pollIds = polls.map(Poll::getId).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);

        List<PollResponse> pollResponses = polls.map(poll -> modelMapper.pollToPollResponse(poll,
                user,
                pollUserVoteMap.getOrDefault(poll.getId(), null),
                choiceVoteCountMap)).getContent();

        return pollsWithContent(polls, pollResponses);
    }

    @Override
    public PollSummary createPoll(PollRequest pollRequest) {
        Poll poll = new Poll();
        poll.setQuestion(pollRequest.getQuestion());

        pollRequest.getChoices().forEach(choiceRequest -> poll.addChoice(new Choice(choiceRequest.getText())));

        Instant now = Instant.now();
        Instant expirationDateTime = now.plus(Duration.ofDays(pollRequest.getPollLength().getDays()))
                .plus(Duration.ofHours(pollRequest.getPollLength().getHours()));

        poll.setExpirationDateTime(expirationDateTime);

        Poll saved = pollRepository.save(poll);
        return PollSummary.builder()
                .id(saved.getId())
                .question(saved.getQuestion())
                .creationDateTime(saved.getCreatedAt())
                .build();
    }

    @Override
    public PollResponse getPollById(Long pollId, UserPrincipal currentUser) {
        Poll poll = findById(pollId);

        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);

        Map<Long, Long> choiceVotesMap = collectToChoiceMap(votes);

        UserSummary user = userService.findById(poll.getCreatedBy());

        Long userVote = null;
        if (currentUser != null) {
            Vote vote = voteRepository.findByUserIdAndPollId(currentUser.getId(), pollId);
            if (vote != null) {
                userVote = vote.getChoice().getId();
            }
        }

        return modelMapper.pollToPollResponse(poll, user, userVote, choiceVotesMap);
    }

    @Override
    public PollResponse vote(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser) {
        Poll poll = findById(pollId);

        if (poll.getExpirationDateTime().isBefore(Instant.now())) {
            throw new BadRequestException("Poll expired");
        }

        User user = userRepository.getOne(currentUser.getId());

        Choice selectedChoice = poll.getChoices().stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Choice", "id", voteRequest.getChoiceId()));

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setUser(user);
        vote.setChoice(selectedChoice);

        try {
            vote = voteRepository.save(vote);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Sorry! You have already cast your vote in this poll");
        }

        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);

        Map<Long, Long> choiceVotesMap = collectToChoiceMap(votes);

        UserSummary creator = userService.findById(poll.getCreatedBy());

        return modelMapper.pollToPollResponse(poll, creator, vote.getChoice().getId(), choiceVotesMap);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > Constants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + Constants.MAX_PAGE_SIZE);
        }
    }

    private PagedResponse<PollResponse> pollsWithContent(Page<Poll> polls, List<PollResponse> content) {
        Poll last = polls.getContent().get(polls.getContent().size() - 1);

        return getPollResponsePagedResponse(polls, content, last == null ? null : last.getId());
    }

    private PagedResponse<PollResponse> idsWithContent(Page<Long> polls, List<PollResponse> content) {
        Long last = polls.getContent().get(polls.getContent().size() - 1);

        return getPollResponsePagedResponse(polls, content, last);
    }

    private PagedResponse<PollResponse> getPollResponsePagedResponse(Page<?> polls,
                                                                     List<PollResponse> content,
                                                                     Long next) {
        if (polls.isLast()) {
            next = null;
        }
        return PagedResponse.<PollResponse>builder()
                .content(content)
                .remaining(polls.getTotalElements())
                .last(polls.isLast())
                .next(next)
                .build();
    }

    private Poll findById(Long pollId) {
        return pollRepository.findById(pollId).orElseThrow(
                () -> new ResourceNotFoundException("Poll", "id", Long.toString(pollId)));
    }

    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds) {
        return collectToChoiceMap(voteRepository.countByPollIdInGroupByChoiceId(pollIds));
    }

    private Map<Long, Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds) {
        if (currentUser != null) {
            return voteRepository
                    .findByUserIdAndPollIdIn(currentUser.getId(), pollIds)
                    .stream()
                    .collect(Collectors.toMap(vote -> vote.getPoll().getId(), vote -> vote.getChoice().getId()));
        }
        return Collections.emptyMap();
    }

    private Map<Long, UserSummary> getPollCreatorMap(List<Poll> polls) {
        List<Long> creatorIds = polls.stream()
                .map(Poll::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        return userService
                .findByIdIn(creatorIds)
                .stream()
                .collect(Collectors.toMap(UserSummary::getId, Function.identity()));
    }

    private Map<Long, Long> collectToChoiceMap(List<ChoiceVoteCount> votes) {
        return votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
    }

    private PageRequest pageableByCreatedAt(int page, int size) {
        return PageRequest.of(page, size, createdAtSort());
    }

    private Sort createdAtSort() {
        return new Sort(Sort.Direction.DESC, "createdAt");
    }
}
