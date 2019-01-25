package com.example.movies.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.movies.MainActivity;
import com.example.movies.model.Movie;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloaderAsyncTask extends AsyncTask<Movie, Object, Object> {

    private MainActivity mainActivity;

    public ImageDownloaderAsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Object doInBackground(Movie... objects) {
        URL url = null;
        Bitmap bitmap;
        try {
            url = new URL(objects[0].getImageUrl());
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            objects[0].setImage(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        mainActivity.adapter.notifyDataSetChanged();
    }
}
