package ru.andrey.poll.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.andrey.poll.MainActivity;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, PollModule.class})
public interface NetworkComponent {

    void inject(MainActivity mainActivity);
}
