package com.example.movies_thread.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.movies_thread.MainActivity;
import com.example.movies_thread.R;
import com.example.movies_thread.model.Movie;

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

        this.mainActivity.handlerTui.post(new Runnable() {
            @Override
            public void run() {
                mainActivity.adapter.notifyDataSetChanged();
            }
        });

        /*mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.adapter.notifyDataSetChanged();
            }
        });*/
    }

}
