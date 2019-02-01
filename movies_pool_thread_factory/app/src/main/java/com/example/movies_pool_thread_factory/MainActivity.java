package com.example.movies_pool_thread_factory;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.movies_pool_thread_factory.adapter.ListAdapter;
import com.example.movies_pool_thread_factory.model.Movie;
import com.example.movies_pool_thread_factory.utils.ImageDownloaderThread;
import com.example.movies_pool_thread_factory.utils.MyThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public ArrayAdapter<Movie> adapter;
    private Button refresh;
    private Button add;
    public static Handler handlerTui = new Handler(Looper.getMainLooper());
    public HandlerThread handlerThread = new HandlerThread("handlerThread");
    public Handler handler2;
    private ThreadPoolExecutor threadPoolExecutor;
    private BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
    private MyThreadFactory myThreadFactory = new MyThreadFactory();

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

        threadPoolExecutor = new ThreadPoolExecutor(1, 3, 1, TimeUnit.SECONDS, mDecodeWorkQueue);
        threadPoolExecutor.setThreadFactory(myThreadFactory);
        refresh = findViewById(R.id.buttonRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* ThreadPool */
                for (Movie movie : movies) {
                    ImageDownloaderThread imageDownloaderThread = new ImageDownloaderThread(movie, MainActivity.this);
                    threadPoolExecutor.execute(imageDownloaderThread);
                }
            }
        });

        moviesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.remove(adapter.getItem(i));
                return true;
            }
        });

    }
}
