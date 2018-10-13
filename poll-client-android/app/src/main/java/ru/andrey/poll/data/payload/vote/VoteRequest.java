package ru.andrey.poll.data.payload.vote;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class VoteRequest {

    private Long choiceId;

    public static VoteRequest of(long id) {
        return VoteRequest.builder().choiceId(id).build();
    }
}
