package ru.andrey.poll.data.network.config;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.andrey.poll.data.repository.TokenRepository;

@Singleton
public class OkHttpClientConfig {

    private Cache cache;
    private Lazy<TokenRepository> tokenRepository;

    @Inject
    OkHttpClientConfig(Cache cache,
                       Lazy<TokenRepository> tokenRepository) {
        this.cache = cache;
        this.tokenRepository = tokenRepository;
    }

    public OkHttpClient buildOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(this::authInterceptor)
                .cache(cache)
                .build();
    }


    private Response authInterceptor(Interceptor.Chain chain) throws IOException {

        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        builder.header("Accept", "application/json");

        String token = tokenRepository.get().getSavedToken();
        setAuthHeader(builder, token);

        request = builder.build();
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            synchronized (OkHttpClientConfig.class) {
                String currentToken = tokenRepository.get().getSavedToken();

                if (currentToken == null || currentToken.equals(token)) {
                    try {
                        token = tokenRepository.get()
                                .getToken()
                                .blockingGet();
                    } catch (RuntimeException e) {
                        tokenRepository.get().invalidateToken();
                    }
                }
            }

            if (token != null) {
                setAuthHeader(builder, token);
                request = builder.build();
                return chain.proceed(request);
            }
        }

        return response;
    }


    private void setAuthHeader(Request.Builder builder, String token) {
        if (token != null) {
            builder.header("Authorization", String.format("Bearer %s", token));
        }
    }
}

