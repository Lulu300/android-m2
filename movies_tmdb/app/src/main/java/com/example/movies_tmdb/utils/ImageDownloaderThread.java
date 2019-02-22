package com.example.movies_tmdb.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.movies_tmdb.MainActivity;
import com.example.movies_tmdb.model.Movie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloaderThread implements Runnable {

    private Movie movie;
    WeakReference<MainActivity> wrMainActivity;

    public ImageDownloaderThread(Movie movie, MainActivity mainActivity){
        this.movie = movie;
        this.wrMainActivity = new WeakReference<MainActivity>(mainActivity);
    }

    public void run(){
        URL url = null;
        Bitmap bitmap;
        try {
            url = new URL(movie.getImageUrl());
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            OutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            movie.setImage(((ByteArrayOutputStream) stream).toByteArray());
            /* bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            movie.setImage(bitmap); */
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final MainActivity mainActivity = wrMainActivity.get();
        if (mainActivity != null) {
            mainActivity.handlerTui.post(new Runnable() {
                @Override
                public void run() {
                    mainActivity.adapter.notifyDataSetChanged();
                }
            });
        }

        /*mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.adapter.notifyDataSetChanged();
            }
        });*/
    }

}
