package ru.andrey.poll.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class User {

    private Long id;
    private String username;
    private String name;
}