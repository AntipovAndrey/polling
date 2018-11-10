package ru.andrey.poll.payload.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserIdentityExistence {

    private Boolean available;

    public static UserIdentityExistence of(Boolean available) {
        return new UserIdentityExistence(available);
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean isAvailable() {
        return available;
    }
}
