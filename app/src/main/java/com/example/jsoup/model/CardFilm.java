package com.example.jsoup.model;

import androidx.annotation.NonNull;

public class CardFilm {
    String title;
    String url;
    String descr;
    String urlImage;
    String rating;

    public CardFilm(String title, String url, String descr, String urlImage) {
        this.title = title;
        this.url = url;
        this.descr = descr;
        this.urlImage = urlImage;
    }

    public String getRating() {
        return rating;
    }

    public CardFilm(String title, String url, String descr, String urlImage, String rating) {
        this.title = title;
        this.url = url;
        this.descr = descr;
        this.urlImage = urlImage;
        this.rating = rating;
    }

    public CardFilm() {

    }

    public String getUrl() {
        return url;
    }

    public String getDescr() {
        return descr;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @NonNull
    @Override
    public String toString() {
        return "CardFilm{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", descr='" + descr + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getUrlImage() {
        return urlImage;
    }
}
