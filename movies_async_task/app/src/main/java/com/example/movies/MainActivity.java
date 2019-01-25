package com.example.movies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.movies.adapter.ListAdapter;
import com.example.movies.model.Movie;
import com.example.movies.utils.ImageDownloaderAsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    public ArrayAdapter<Movie> adapter;
    private Button refresh;
    private Button add;

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

        refresh = findViewById(R.id.buttonRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* Async Task */
                for (Movie movie : movies) {
                    ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(MainActivity.this);
                    // imageDownloaderAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, movie);
                    imageDownloaderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie);
                }
            }
        });
    }
}
