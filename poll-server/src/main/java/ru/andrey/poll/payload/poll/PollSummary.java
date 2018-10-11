package ru.andrey.poll.payload.poll;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class PollSummary {

    private Long id;
    private String question;
    private Instant creationDateTime;
}
