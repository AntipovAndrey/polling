package ru.andrey.poll.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.andrey.poll.data.payload.PagedResponse;
import ru.andrey.poll.data.payload.choice.ChoiceResponse;
import ru.andrey.poll.data.payload.poll.PollResponse;
import ru.andrey.poll.data.payload.user.UserSummary;
import ru.andrey.poll.domain.model.Choice;
import ru.andrey.poll.domain.model.Page;
import ru.andrey.poll.domain.model.Poll;
import ru.andrey.poll.domain.model.User;

@Singleton
public class Mapper {

    @Inject
    public Mapper() {
    }


    public Page<Poll> pagedResponseToPage(@NonNull PagedResponse<PollResponse> response) {
        List<PollResponse> responseContent = response.getContent();
        List<Poll> content = new ArrayList<>();
        for (PollResponse pollResponse : responseContent) {
            content.add(pollResponseToPoll(pollResponse));
        }
        return Page.<Poll>builder()
                .content(content)
                .page(response.getPage())
                .size(response.getSize())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .last(response.isLast())
                .build();
    }

    public Poll pollResponseToPoll(PollResponse pollResponse) {
        List<ChoiceResponse> choiceResponses = pollResponse.getChoices();
        List<Choice> choices = choiceResponsesToChoices(choiceResponses);

        return Poll.builder()
                .choices(choices)
                .id(pollResponse.getId())
                .question(pollResponse.getQuestion())
                .createdBy(userSummaryToUser(pollResponse.getCreatedBy()))
                .creationDateTime(pollResponse.getCreationDateTime())
                .expirationDateTime(pollResponse.getExpirationDateTime())
                .isExpired(pollResponse.getIsExpired())
                .selectedChoice(pollResponse.getSelectedChoice())
                .totalVotes(pollResponse.getTotalVotes())
                .build();
    }

    @NonNull
    List<Choice> choiceResponsesToChoices(@NonNull List<ChoiceResponse> choiceResponses) {
        List<Choice> choices = new ArrayList<>();

        for (ChoiceResponse choiceResponse : choiceResponses) {
            choices.add(Choice.builder()
                    .id(choiceResponse.getId())
                    .text(choiceResponse.getText())
                    .voteCount(choiceResponse.getVoteCount())
                    .build());
        }
        return choices;
    }

    @NonNull
    public User userSummaryToUser(UserSummary userSummary) {
        return User.builder()
                .id(userSummary.getId())
                .name(userSummary.getName())
                .username(userSummary.getUsername())
                .build();
    }
}
