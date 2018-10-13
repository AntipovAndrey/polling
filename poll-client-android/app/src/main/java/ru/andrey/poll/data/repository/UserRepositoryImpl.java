package ru.andrey.poll.data.repository;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Retrofit;
import ru.andrey.poll.data.Mapper;
import ru.andrey.poll.data.network.api.UserApi;
import ru.andrey.poll.domain.model.Page;
import ru.andrey.poll.domain.model.Poll;
import ru.andrey.poll.domain.model.User;
import ru.andrey.poll.domain.model.UserProfile;
import ru.andrey.poll.domain.repository.UserRepository;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private final UserApi api;
    private final Mapper mapper;

    @Inject
    UserRepositoryImpl(Retrofit retrofit, Mapper mapper) {
        api = retrofit.create(UserApi.class);
        this.mapper = mapper;
    }

    @Override
    public Completable register(@NonNull String name, @NonNull String username,
                                @NonNull String email, @NonNull String password) {
        return null;
    }

    @Override
    public Single<String> auth(@NonNull String emailOrPassword) {
        return null;
    }

    @Override
    public Single<Boolean> usernameNotTaken(@NonNull String username) {
        return null;
    }

    @Override
    public Single<Boolean> emailNotTaken(@NonNull String password) {
        return null;
    }

    @Override
    public Single<UserProfile> getUserProfile(@NonNull String username) {
        return null;
    }

    @Override
    public Single<User> getCurrent() {
        return api.getCurrent()
                .map(mapper::userSummaryToUser);
    }

    @Override
    public Single<Page<Poll>> getPollsCreatedBy(@NonNull String username, int page, int size) {
        return null;
    }

    @Override
    public Single<Page<Poll>> getPollsVotedBy(@NonNull String username, int page, int size) {
        return null;
    }
}
