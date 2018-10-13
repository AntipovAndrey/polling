package ru.andrey.poll.data.payload.choice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class ChoiceRequest {

    private String text;

    public static ChoiceRequest withChoice(String choice) {
        return ChoiceRequest.builder().text(choice).build();
    }
}
