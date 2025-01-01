package com.example.jsoup.model;

public class CardFilm {
    String title;
    String url;
    String descr;
    String urlImage;

    public CardFilm(String title, String url, String urlImage) {
        this.title = title;
        this.url = url;
        this.urlImage = urlImage;
    }

    public CardFilm(String title, String url, String descr, String urlImage) {
        this.title = title;
        this.url = url;
        this.descr = descr;
        this.urlImage = urlImage;
    }

    public CardFilm(String descr) {
        this.descr = descr;
    }

    public CardFilm() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String setTitle(String title) {
        this.title = title;
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String getUrlImage() {
        return urlImage;
    }
}
