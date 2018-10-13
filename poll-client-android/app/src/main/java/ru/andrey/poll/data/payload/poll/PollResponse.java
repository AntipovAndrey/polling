package ru.andrey.poll.data.payload.poll;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.andrey.poll.data.payload.choice.ChoiceResponse;
import ru.andrey.poll.data.payload.user.UserSummary;

@Getter
@Setter
@Builder
public class PollResponse {

    private Long id;
    private String question;
    private List<ChoiceResponse> choices;
    private UserSummary createdBy;
    private Date creationDateTime;
    private Date expirationDateTime;
    private Boolean isExpired;
    private Long selectedChoice;
    private Long totalVotes;
}
