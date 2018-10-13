package ru.andrey.poll.domain.repository;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.andrey.poll.domain.model.Page;
import ru.andrey.poll.domain.model.Poll;

public interface PollRepository {

    Single<Page<Poll>> getAll(int page, int size);

    Completable createPoll(@NonNull String question, List<String> choices, int days, int hours);

    Single<Poll> getById(long id);

    Single<Poll> vote(long pollId, long choiceId);
}
