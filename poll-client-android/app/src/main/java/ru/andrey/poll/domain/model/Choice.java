package ru.andrey.poll.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Choice {

    private Long id;
    private String text;
    private long voteCount;
}
