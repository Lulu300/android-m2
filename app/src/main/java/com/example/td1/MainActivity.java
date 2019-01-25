package com.example.td1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.td1.adapter.ListAdapter;
import com.example.td1.model.Movie;
import com.example.td1.utils.ImageDownloaderAsyncTask;
import com.example.td1.utils.ImageDownloaderThread;

import java.util.ArrayList;
import java.util.List;

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
        movies.add(new Movie("https://image.tmdb.org/t/p/w1280/vpQxNHhS6BxmwKiWoUUPancE4mV.jpg", "Your Name", "2016", "Naoko Yamada", "Reiko Yoshida"));
        movies.add(new Movie("https://image.tmdb.org/t/p/w1280/xjeYI75uMBtBjNlJ0cDJZDFg5Yv.jpg", "Silent Voice", "2016", "Naoko Yamada", "Reiko Yoshida"));
        ListView moviesView = (ListView) findViewById(R.id.listMovies);
        adapter = new ListAdapter(this, movies);
        moviesView.setAdapter(adapter);

        refresh = findViewById(R.id.buttonRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* Async Task */
                for (Movie movie : movies) {
                    ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(MainActivity.this);
                    imageDownloaderAsyncTask.execute(movie);
                }
            }
        });
    }
}
