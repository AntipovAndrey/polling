package ru.andrey.poll.data.payload.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserProfile {

    private Long id;
    private String username;
    private String name;
    private Date joinedAt;
    private Long pollCount;
    private Long voteCount;
}
