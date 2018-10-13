package ru.andrey.poll.data.payload.poll;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.andrey.poll.data.payload.choice.ChoiceRequest;

@Getter
@Setter
@Builder
public class PollRequest {

    private String question;
    private List<ChoiceRequest> choices;
    private PollLength pollLength;
}
