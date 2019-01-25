package com.example.movies.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.movies.MainActivity;
import com.example.movies.model.Movie;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloaderThread implements Runnable {

    private Movie movie;
    MainActivity mainActivity;

    public ImageDownloaderThread(Movie movie, MainActivity mainActivity){
        this.movie = movie;
        this.mainActivity = mainActivity;
    }

    public void run(){
        URL url = null;
        Bitmap bitmap;
        try {
            url = new URL(movie.getImageUrl());
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            movie.setImage(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.adapter.notifyDataSetChanged();
            }
        });
    }

}
