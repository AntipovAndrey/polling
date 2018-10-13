package ru.andrey.poll.data.payload.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSummary {

    private Long id;
    private String username;
    private String name;
}
