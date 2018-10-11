package ru.andrey.poll.payload.poll;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.andrey.poll.payload.choice.ChoiceResponse;
import ru.andrey.poll.payload.user.UserSummary;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class PollResponse {

    private Long id;
    private String question;
    private List<ChoiceResponse> choices;
    private UserSummary createdBy;
    private Instant creationDateTime;
    private Instant expirationDateTime;
    private Boolean isExpired;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long selectedChoice;
    private Long totalVotes;
}
