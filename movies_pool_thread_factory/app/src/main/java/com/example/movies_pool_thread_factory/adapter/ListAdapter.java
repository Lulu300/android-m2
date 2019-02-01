package com.example.movies_pool_thread_factory.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.movies_pool_thread_factory.R;
import com.example.movies_pool_thread_factory.model.Movie;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Movie> {

    private LinearLayout item;
    private ImageView movieImage;
    private TextView movieName;
    private TextView movieDate;
    private TextView movieProd;
    private TextView movieReal;

    public ListAdapter(@NonNull Context context, @NonNull List<Movie> objects) {
        super(context, R.layout.activity_list_items, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Movie movie = (Movie) getItem(position);

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_items,parent, false);
        }

        item = (LinearLayout) view.findViewById(R.id.activity_list_items_item);
        movieImage = (ImageView) view.findViewById(R.id.imageViewMovie);
        movieName = (TextView) view.findViewById(R.id.textViewName);
        movieDate = (TextView) view.findViewById(R.id.textViewDate);
        movieProd = (TextView) view.findViewById(R.id.textViewProd);
        movieReal = (TextView) view.findViewById(R.id.textViewReal);

        movieName.setText(movie.getName());
        movieDate.setText(movie.getDate());
        movieProd.setText(movie.getProd());
        movieReal.setText(movie.getReal());

        if(movie.getImage() != null) {
            movieImage.setImageBitmap(movie.getImage());
        } else {
            movieImage.setImageBitmap(null);
        }

        if(position % 2 == 0) {
            item.setBackgroundColor(Color.WHITE);
        } else{
            item.setBackgroundColor(Color.GRAY);
        }

        return view;
    }

}
