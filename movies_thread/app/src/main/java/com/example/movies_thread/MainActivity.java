package com.example.movies_thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.movies_thread.adapter.ListAdapter;
import com.example.movies_thread.model.Movie;
import com.example.movies_thread.utils.ImageDownloaderThread;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public ArrayAdapter<Movie> adapter;
    private Button refresh;
    private Button refresh2;
    private Button add;
    public static Handler handlerTui = new Handler(Looper.getMainLooper());
    public HandlerThread handlerThread = new HandlerThread("handlerThread");
    public Handler handler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<Movie> movies = new ArrayList<Movie>();
        movies.add(new Movie("https://image.tmdb.org/t/p/w1280/xjeYI75uMBtBjNlJ0cDJZDFg5Yv.jpg", "Silent Voice", "2016", "Naoko Yamada", "Reiko Yoshida"));
        movies.add(new Movie("https://image.tmdb.org/t/p/w1280/vpQxNHhS6BxmwKiWoUUPancE4mV.jpg", "Your Name", "2016", "Makoto Shinkai", "Makoto Shinkai"));
        movies.add(new Movie("https://image.tmdb.org/t/p/w1280/77Z0g5fc1qWJ7SfHyeiHsMkYx5O.jpg", "Spider-Man : Homecoming ", "2017", "Jon Watts", "Jon Watts"));
        ListView moviesView = (ListView) findViewById(R.id.listMovies);
        adapter = new ListAdapter(this, movies);
        moviesView.setAdapter(adapter);

        handlerThread.start();
        handler2 = new Handler(handlerThread.getLooper());

        refresh = findViewById(R.id.buttonRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* Thread */
                for (Movie movie : movies) {
                    ImageDownloaderThread imageDownloaderThread = new ImageDownloaderThread(movie, MainActivity.this);
                    new Thread(imageDownloaderThread).start();
                }
            }
        });

        refresh2 = findViewById(R.id.buttonRefresh2);
        refresh2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* Thread */
                for (Movie movie : movies) {
                    ImageDownloaderThread imageDownloaderThread = new ImageDownloaderThread(movie, MainActivity.this);
                    handler2.post(imageDownloaderThread);
                }
            }
        });
    }
}
