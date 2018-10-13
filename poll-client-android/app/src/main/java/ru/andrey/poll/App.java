package ru.andrey.poll;

import android.app.Application;

import ru.andrey.poll.di.AppModule;
import ru.andrey.poll.di.DaggerNetworkComponent;
import ru.andrey.poll.di.NetworkComponent;
import ru.andrey.poll.di.NetworkModule;

public class App extends Application {

    private NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        networkComponent = DaggerNetworkComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule(BuildConfig.SERVER_URL))
                .build();
    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }
}
