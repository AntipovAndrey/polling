package ru.andrey.poll.data.network.api;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.andrey.poll.data.payload.PagedResponse;
import ru.andrey.poll.data.payload.poll.PollRequest;
import ru.andrey.poll.data.payload.poll.PollResponse;
import ru.andrey.poll.data.payload.vote.VoteRequest;

public interface PollApi {

    @GET("/api/polls")
    Single<PagedResponse<PollResponse>> getPolls(@Query("page") int page,
                                                 @Query("size") int size);

    @GET("/api/polls?page=0&size=30")
    Single<PagedResponse<PollResponse>> getPolls();

    @GET("/api/polls/{pollId}")
    Single<PollResponse> getPollById(@Path("pollId") long pollId);

    @POST("/api/polls/")
    Completable createPoll(@NonNull @Body PollRequest pollRequest);

    @POST("/api/polls/{pollId}/votes")
    Single<PollResponse> vote(@Path("pollId") long pollId,
                              @NonNull @Body VoteRequest voteRequest);
}
