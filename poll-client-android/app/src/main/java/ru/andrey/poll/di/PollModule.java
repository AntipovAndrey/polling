package ru.andrey.poll.di;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import ru.andrey.poll.data.repository.PollRepositoryImpl;
import ru.andrey.poll.data.repository.UserRepositoryImpl;
import ru.andrey.poll.domain.repository.PollRepository;
import ru.andrey.poll.domain.repository.UserRepository;

@Module
public abstract class PollModule {

    @Binds
    @Singleton
    public abstract PollRepository providePollRepository(PollRepositoryImpl pollRepository);

    @Binds
    @Singleton
    public abstract UserRepository provideUserRepository(UserRepositoryImpl userRepository);
}
