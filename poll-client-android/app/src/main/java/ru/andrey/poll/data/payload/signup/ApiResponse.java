package ru.andrey.poll.data.payload.signup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiResponse {

    private Boolean success;
    private String message;

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }

    public static ApiResponse ok(String message) {
        return new ApiResponse(true, message == null ? "" : message);
    }
}
