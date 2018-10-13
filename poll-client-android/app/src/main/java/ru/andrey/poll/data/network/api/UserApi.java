package ru.andrey.poll.data.network.api;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.andrey.poll.data.payload.PagedResponse;
import ru.andrey.poll.data.payload.login.JwtAuthenticationResponse;
import ru.andrey.poll.data.payload.login.LoginRequest;
import ru.andrey.poll.data.payload.poll.PollResponse;
import ru.andrey.poll.data.payload.signup.ApiResponse;
import ru.andrey.poll.data.payload.signup.SignUpRequest;
import ru.andrey.poll.data.payload.user.UserIdentityAvailability;
import ru.andrey.poll.data.payload.user.UserProfile;
import ru.andrey.poll.data.payload.user.UserSummary;

public interface UserApi {

    @POST("/api/auth/signin")
    Single<JwtAuthenticationResponse> auth(@NonNull @Body LoginRequest loginRequest);

    @POST("/api/auth/signup")
    Single<ApiResponse> register(@NonNull @Body SignUpRequest signUpRequest);

    @GET("/api/user/me")
    Single<UserSummary> getCurrent();

    @GET("/api/user/checkUsernameAvailability")
    Single<UserIdentityAvailability> availableByName(@Query("username") String username);

    @GET("/api/user/checkUsernameAvailability")
    Single<UserIdentityAvailability> availableByEmail(@Query("email") String email);

    @GET("/api/users/{username}")
    Single<UserProfile> getUserProfle(@Path("username") String username);

    @GET("/api/users/{username}/polls")
    Single<PagedResponse<PollResponse>> getPollsCreatedBy(@Path("username") String username,
                                                          @Query("page") int page,
                                                          @Query("size") int size);

    @GET("/api/users/{username}/votes")
    Single<PagedResponse<PollResponse>> getPollsVotedBy(@Path("username") String username,
                                                        @Query("page") int page,
                                                        @Query("size") int size);
}
