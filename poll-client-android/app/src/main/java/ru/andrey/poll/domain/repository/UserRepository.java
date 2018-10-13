package ru.andrey.poll.domain.repository;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.andrey.poll.domain.model.Page;
import ru.andrey.poll.domain.model.Poll;
import ru.andrey.poll.domain.model.User;
import ru.andrey.poll.domain.model.UserProfile;

public interface UserRepository {

    Completable register(@NonNull String name, @NonNull String username,
                         @NonNull String email, @NonNull String password);

    Single<String> auth(@NonNull String emailOrPassword);

    Single<Boolean> usernameNotTaken(@NonNull String username);

    Single<Boolean> emailNotTaken(@NonNull String password);

    Single<UserProfile> getUserProfile(@NonNull String username);

    Single<User> getCurrent();

    Single<Page<Poll>> getPollsCreatedBy(@NonNull String username, int page, int size);

    Single<Page<Poll>> getPollsVotedBy(@NonNull String username, int page, int size);
}
