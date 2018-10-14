package ru.andrey.poll.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {

    private List<T> content;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long next;
    private long remaining;
    private boolean last;
}
