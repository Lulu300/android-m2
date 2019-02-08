package com.example.movies_permissions_and_cipher;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.movies_permissions_and_cipher.adapter.ListAdapter;
import com.example.movies_permissions_and_cipher.model.Movie;
import com.example.movies_permissions_and_cipher.permission.Permission;
import com.example.movies_permissions_and_cipher.permission.PermissionInformation;
import com.example.movies_permissions_and_cipher.permission.RequestCode;
import com.example.movies_permissions_and_cipher.utils.ImageDownloaderThread;
import com.example.movies_permissions_and_cipher.utils.MyThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends Activity {

    public ArrayAdapter<Movie> adapter;
    private Button refresh;
    private Button add;
    public static Handler handlerTui = new Handler(Looper.getMainLooper());
    public HandlerThread handlerThread = new HandlerThread("handlerThread");
    public Handler handler2;
    private ThreadPoolExecutor threadPoolExecutor;
    private BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
    private MyThreadFactory myThreadFactory = new MyThreadFactory();
    private List<PermissionInformation> permissionsInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.permissionsInfos = new ArrayList<>();
        final Permission permission = new Permission(this, permissionsInfos);
        permission.askPermission();

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
                String[] nonCritiquePerms = new String[]{CAMERA};
                MainActivity.this.requestPermissions(nonCritiquePerms, RequestCode.NON_CRITIQUE.getCode());
                if (MainActivity.this.permissionsInfos.contains(new PermissionInformation(CAMERA, true))) {
                    /* ThreadPool */
                    for (Movie movie : movies) {
                        ImageDownloaderThread imageDownloaderThread = new ImageDownloaderThread(movie, MainActivity.this);
                        threadPoolExecutor.execute(imageDownloaderThread);
                    }
                } else {
                    CharSequence text = "Permission Denied :(";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(MainActivity.this.getApplicationContext(), text, duration);
                    toast.show();
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

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i(this.getClass().getName(), "" + requestCode);
        RequestCode rc = RequestCode.getRequestCode(requestCode);
        for (int i = 0; i < permissions.length; i++) {
            PermissionInformation permInfo = new PermissionInformation(permissions[i]);
            switch(rc) {
                case CRITIQUE:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        permInfo.setGranted(true);
                    } else {
                        this.finishAffinity();
                    }
                    break;
                case NON_CRITIQUE:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        permInfo.setGranted(true);
                    }
                    break;
            }
            this.permissionsInfos.add(permInfo);
        }
        // updateViewPerms();
    }

}
