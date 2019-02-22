package com.example.movies_tmdb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.movies_tmdb.MainActivity;
import com.example.movies_tmdb.model.Movie;
import com.example.movies_tmdb.model.MovieDetails;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MovieDetailsDownloader implements Runnable {

    WeakReference<MainActivity> wrMainActivity;
    private String apiKey = "INSERT_YOUR_API_KEY";
    private int movieId;

    public MovieDetailsDownloader(MainActivity mainActivity, int movieId){
        this.wrMainActivity = new WeakReference<MainActivity>(mainActivity);
        this.movieId = movieId;
    }

    public void run() {
        Movie movie = new Movie();
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url(String.format("https://api.themoviedb.org/3/movie/%s?language=fr-FR&api_key=%s", this.movieId, this.apiKey))
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            String bodyResponse = response.body().string();
            MovieDetails movieDetails = new Gson().fromJson(bodyResponse, MovieDetails.class);
            URL url = new URL(String.format("https://image.tmdb.org/t/p/w1280%s", movieDetails.getPosterPath()));
            URLConnection conn = url.openConnection();
            Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            OutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            movie.setImage(((ByteArrayOutputStream) stream).toByteArray());
            movie.setDate(movieDetails.getReleaseDate().substring(0, 4));
            movie.setName(movieDetails.getOriginalTitle());
            movie.setReal(String.format("%s/10", movieDetails.getVoteAverage().toString()));
            movie.setProd(movieDetails.getStatus());
            final MainActivity mainActivity = wrMainActivity.get();
            if (mainActivity != null) {
                mainActivity.movies.add(movie);
                // mainActivity.adapter.add(movie);
                mainActivity.handlerTui.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.adapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            final MainActivity mainActivity = wrMainActivity.get();
            if (mainActivity != null) {
                mainActivity.adapter.add(movie);
                mainActivity.handlerTui.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(mainActivity.getApplicationContext(), "ID incorrect", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }

    }

}
