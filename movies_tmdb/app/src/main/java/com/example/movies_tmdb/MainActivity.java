package com.example.movies_tmdb;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.movies_tmdb.adapter.ListAdapter;
import com.example.movies_tmdb.model.Movie;
import com.example.movies_tmdb.permission.Permission;
import com.example.movies_tmdb.permission.PermissionInformation;
import com.example.movies_tmdb.permission.RequestCode;
import com.example.movies_tmdb.utils.ImageDownloaderThread;
import com.example.movies_tmdb.utils.MovieDetailsDownloader;
import com.example.movies_tmdb.utils.MyThreadFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends Activity {

    public ArrayAdapter<Movie> adapter;
    private Button refresh;
    private Button add;
    private Button save;
    private Button load;
    private Button clear;
    public static Handler handlerTui = new Handler(Looper.getMainLooper());
    public HandlerThread handlerThread = new HandlerThread("handlerThread");
    public Handler handler2;
    private ThreadPoolExecutor threadPoolExecutor;
    private BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
    private MyThreadFactory myThreadFactory = new MyThreadFactory();
    private List<PermissionInformation> permissionsInfos;
    public List<Movie> movies = new ArrayList<Movie>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.permissionsInfos = new ArrayList<>();
        final Permission permission = new Permission(this, permissionsInfos);
        permission.askPermission();

        // movies.add(new Movie("https://image.tmdb.org/t/p/w1280/xjeYI75uMBtBjNlJ0cDJZDFg5Yv.jpg", "Silent Voice", "2016", "Naoko Yamada", "Reiko Yoshida"));
        // movies.add(new Movie("https://image.tmdb.org/t/p/w1280/vpQxNHhS6BxmwKiWoUUPancE4mV.jpg", "Your Name", "2016", "Makoto Shinkai", "Makoto Shinkai"));
        // movies.add(new Movie("https://image.tmdb.org/t/p/w1280/77Z0g5fc1qWJ7SfHyeiHsMkYx5O.jpg", "Spider-Man : Homecoming ", "2017", "Jon Watts", "Jon Watts"));
        ListView moviesView = (ListView) findViewById(R.id.listMovies);
        adapter = new ListAdapter(this, movies);
        moviesView.setAdapter(adapter);
        handlerThread.start();
        handler2 = new Handler(handlerThread.getLooper());

        threadPoolExecutor = new ThreadPoolExecutor(1, 3, 1, TimeUnit.SECONDS, mDecodeWorkQueue);
        threadPoolExecutor.setThreadFactory(myThreadFactory);
        refresh = findViewById(R.id.buttonRefresh);
        save = findViewById(R.id.buttonSave);
        add = findViewById(R.id.buttonAddMovie);
        load = findViewById(R.id.buttonLoad);
        clear = findViewById(R.id.buttonClear);

        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                movies.clear();
                adapter.notifyDataSetChanged();
            }
        });

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

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveCipher();
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadCipher();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addMovie();
            }
        });
    }

    public void saveCipher() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.cipher_password);
        dialog.setTitle("Cipher Key");
        dialog.setCancelable(true);
        final EditText keyEditText = dialog.findViewById(R.id.keyCipher);
        Button button = dialog.findViewById(R.id.buttonCipherOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = keyEditText.getText().toString();
                byte[] keyBytes = new byte[16];
                System.arraycopy(key.getBytes(), 0, keyBytes, 16 - key.length(), key.length());
                dialog.cancel();
                Context context = MainActivity.this.getApplicationContext();
                try {
                    File file = new File(context.getExternalCacheDir().getAbsolutePath(), "movies_td");
                    Toast toast = Toast.makeText(context, file.getAbsolutePath(), Toast.LENGTH_SHORT);
                    toast.show();
                    FileOutputStream stream = new FileOutputStream(file);
                    SecretKeySpec skey = new SecretKeySpec(keyBytes, "AES");
                    Cipher enc = Cipher.getInstance("AES");
                    enc.init(Cipher.ENCRYPT_MODE, skey);
                    CipherOutputStream cos = new CipherOutputStream(stream, enc);
                    ObjectOutputStream os = new ObjectOutputStream(cos);
                    os.writeObject(movies);
                    os.flush();
                    os.close();
                    cos.close();
                    stream.close();
                } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        dialog.show();
    }

    public void loadCipher() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.cipher_password);
        dialog.setTitle("Cipher Key");
        dialog.setCancelable(true);
        final EditText keyEditText = dialog.findViewById(R.id.keyCipher);
        Button button = dialog.findViewById(R.id.buttonCipherOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = keyEditText.getText().toString();
                byte[] keyBytes = new byte[16];
                System.arraycopy(key.getBytes(), 0, keyBytes, 16 - key.length(), key.length());
                dialog.cancel();
                Context context = MainActivity.this.getApplicationContext();
                try {
                    File file = new File(context.getExternalCacheDir().getAbsolutePath(), "movies_td");
                    Toast toast = Toast.makeText(context, file.getAbsolutePath(), Toast.LENGTH_SHORT);
                    toast.show();
                    SecretKeySpec skey = new SecretKeySpec(keyBytes, "AES");
                    Cipher enc = Cipher.getInstance("AES");
                    enc.init(Cipher.DECRYPT_MODE, skey);
                    FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                    CipherInputStream cis = new CipherInputStream(fis, enc);
                    ObjectInputStream input = new ObjectInputStream(cis);
                    ArrayList<Movie> movies2 = (ArrayList<Movie>) input.readObject();
                    input.close();
                    cis.close();
                    fis.close();
                    movies.clear();
                    movies.addAll(movies2);
                } catch (IOException | ClassNotFoundException  | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(context, "Vous n'avez pas saisis la bonne clé", Toast.LENGTH_SHORT);
                    toast.show();
                }
                adapter.notifyDataSetChanged();

            }
        });
        dialog.show();
    }

    public void addMovie() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.movie_id);
        dialog.setTitle("Movie ID");
        dialog.setCancelable(true);
        final EditText keyEditText = dialog.findViewById(R.id.movieId);
        Button button = dialog.findViewById(R.id.movieIdOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int movieId = Integer.valueOf(keyEditText.getText().toString());
                dialog.cancel();
                MovieDetailsDownloader movieDetailsDownloader = new MovieDetailsDownloader(MainActivity.this, movieId);
                threadPoolExecutor.execute(movieDetailsDownloader);
                adapter.notifyDataSetChanged();

            }
        });
        dialog.show();
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
