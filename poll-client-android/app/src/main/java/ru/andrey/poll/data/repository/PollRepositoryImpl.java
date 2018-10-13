package ru.andrey.poll.data.repository;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Retrofit;
import ru.andrey.poll.data.Mapper;
import ru.andrey.poll.data.network.api.PollApi;
import ru.andrey.poll.data.payload.choice.ChoiceRequest;
import ru.andrey.poll.data.payload.poll.PollLength;
import ru.andrey.poll.data.payload.poll.PollRequest;
import ru.andrey.poll.data.payload.vote.VoteRequest;
import ru.andrey.poll.domain.model.Page;
import ru.andrey.poll.domain.model.Poll;
import ru.andrey.poll.domain.repository.PollRepository;

@Singleton
public class PollRepositoryImpl implements PollRepository {

    private PollApi api;
    private Mapper mapper;

    @Inject
    PollRepositoryImpl(Retrofit retrofit, Mapper mapper) {
        api = retrofit.create(PollApi.class);
        this.mapper = mapper;
    }

    @Override
    public Single<Page<Poll>> getAll(int page, int size) {
        return api.getPolls(page, size)
                .map(mapper::pagedResponseToPage);
    }

    @Override
    public Completable createPoll(@NonNull String question,
                                  List<String> choices,
                                  int days,
                                  int hours) {
        List<ChoiceRequest> choiceRequests = new ArrayList<>();
        for (String choice : choices) {
            choiceRequests.add(ChoiceRequest.withChoice(choice));
        }
        PollRequest request = PollRequest.builder()
                .pollLength(PollLength.builder().days(days).hours(hours).build())
                .question(question)
                .choices(choiceRequests)
                .build();
        return api.createPoll(request);
    }

    @Override
    public Single<Poll> getById(long id) {
        return api.getPollById(id)
                .map(mapper::pollResponseToPoll);
    }

    @Override
    public Single<Poll> vote(long pollId, long choiceId) {
        return api.vote(pollId, VoteRequest.of(choiceId))
                .map(mapper::pollResponseToPoll);
    }
}
