package ru.andrey.poll.payload.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserIdentityAvailability {

    private Boolean available;

    public static UserIdentityAvailability of(Boolean available) {
        return new UserIdentityAvailability(available);
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean isAvailable() {
        return available;
    }
}
