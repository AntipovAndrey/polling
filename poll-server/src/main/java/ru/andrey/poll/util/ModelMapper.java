package ru.andrey.poll.util;

import org.springframework.stereotype.Component;
import ru.andrey.poll.model.Choice;
import ru.andrey.poll.model.Poll;
import ru.andrey.poll.payload.choice.ChoiceResponse;
import ru.andrey.poll.payload.poll.PollResponse;
import ru.andrey.poll.payload.user.UserSummary;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ModelMapper {

    public PollResponse pollToPollResponse(Poll poll,
                                           UserSummary creator,
                                           Long userVote,
                                           Map<Long, Long> choiceVoteMap) {
        List<ChoiceResponse> choiceResponses = poll.getChoices()
                .stream()
                .map(c -> choiceToChoiceResponse(c, choiceVoteMap))
                .collect(Collectors.toList());
        return PollResponse.builder()
                .id(poll.getId())
                .question(poll.getQuestion())
                .creationDateTime(poll.getCreatedAt())
                .expirationDateTime(poll.getExpirationDateTime())
                .isExpired(poll.getExpirationDateTime().isBefore(Instant.now()))
                .choices(choiceResponses)
                .createdBy(creator)
                .selectedChoice(userVote)
                .totalVotes(choiceResponses.stream()
                        .mapToLong(ChoiceResponse::getVoteCount)
                        .sum())
                .build();
    }

    public ChoiceResponse choiceToChoiceResponse(Choice choice, Map<Long, Long> choiceVoteMap) {
        long voteCount = 0;
        if (choiceVoteMap.containsKey(choice.getId())) {
            voteCount = choiceVoteMap.get(choice.getId());
        }
        return ChoiceResponse.builder()
                .id(choice.getId())
                .text(choice.getText())
                .voteCount(voteCount)
                .build();
    }
}
