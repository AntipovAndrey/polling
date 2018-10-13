package ru.andrey.poll.domain.model;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Poll {

    private Long id;
    private String question;
    private List<Choice> choices;
    private User createdBy;
    private Date creationDateTime;
    private Date expirationDateTime;
    private Boolean isExpired;
    private Long selectedChoice;
    private Long totalVotes;
}
