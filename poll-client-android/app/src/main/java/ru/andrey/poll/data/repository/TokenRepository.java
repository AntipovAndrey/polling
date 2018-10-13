package ru.andrey.poll.data.repository;

import android.support.annotation.NonNull;

import com.auth0.android.jwt.JWT;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import retrofit2.Retrofit;
import ru.andrey.poll.data.network.api.UserApi;
import ru.andrey.poll.data.payload.login.JwtAuthenticationResponse;
import ru.andrey.poll.data.payload.login.LoginRequest;

@Singleton
public class TokenRepository {

    private String username = "wendar";
    private String password = "qweasdzxc";
    private String token = null;

    private UserApi userApi;

    @Inject
    TokenRepository(Retrofit retrofit) {
        this.userApi = retrofit.create(UserApi.class);
    }

    public void savePassword(@NonNull String password) {
        this.password = password;
    }

    public void saveLogin(@NonNull String usernameOrEmail) {
        this.username = usernameOrEmail;
    }

    public boolean possibleToRefresh() {
        return username != null && password != null;
    }

    public String getSavedToken() {
        return token;
    }

    public void invalidateToken() {
        token = null;
    }

    public Single<String> getToken() {
        if (token == null || new JWT(token).isExpired(10)) {
            return userApi.auth(LoginRequest.builder()
                    .usernameOrEmail(username)
                    .password(password).build())
                    .map(JwtAuthenticationResponse::getAccessToken);
        }

        return Single.just(token);
    }
}
