package ru.andrey.poll;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.andrey.poll.domain.repository.PollRepository;
import ru.andrey.poll.domain.repository.UserRepository;

public class MainActivity extends AppCompatActivity {

    @Inject
    PollRepository pollRepository;
    @Inject
    UserRepository userRepository;

    TextView text;
    TextView error;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((App) getApplication()).getNetworkComponent().inject(this);

        Button all = findViewById(R.id.getAll);
        Button post = findViewById(R.id.post);
        text = findViewById(R.id.text);
        error = findViewById(R.id.error);

        all.setOnClickListener((e) -> {
            text.setText("");
            error.setText("");
            getAll();
        });


        post.setOnClickListener((e) -> {
            text.setText("");
            error.setText("");
            aboutme();
        });

    }

    private void aboutme() {
        userRepository.getCurrent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(gson::toJson)
                .subscribe(s -> text.setText(s), e -> error.setText(e.getMessage()));
    }

    private void getAll() {
        pollRepository.getAll(0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(gson::toJson)
                .subscribe(s -> text.setText(s));
    }
}
