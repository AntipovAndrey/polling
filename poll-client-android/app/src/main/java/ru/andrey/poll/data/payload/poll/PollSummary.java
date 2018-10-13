package ru.andrey.poll.data.payload.poll;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PollSummary {

    private Long id;
    private String question;
    private Date creationDateTime;
}
