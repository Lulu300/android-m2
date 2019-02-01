package com.example.movies_pool_thread_factory.model;

import android.graphics.Bitmap;

public class Movie {

    private String imageUrl;
    private String name;
    private String date;
    private String prod;
    private String real;
    private Bitmap image;

    public Movie(String imageUrl, String name, String date, String prod, String real) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.date = date;
        this.prod = prod;
        this.real = real;
    }

    public Movie() {}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public String getReal() {
        return real;
    }

    public void setReal(String real) {
        this.real = real;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", prod='" + prod + '\'' +
                ", real='" + real + '\'' +
                '}';
    }
}
