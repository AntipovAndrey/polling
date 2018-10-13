package ru.andrey.poll.data.payload.poll;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class PollLength {

    private Integer days;
    private Integer hours;
}
