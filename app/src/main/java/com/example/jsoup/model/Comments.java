package com.example.jsoup.model;

public class Comments {
    String urlavatar;
    String author;
    String comment;
    String time;

    public Comments(String urlavatar, String author, String comment, String time) {
        this.urlavatar = urlavatar;
        this.author = author;
        this.comment = comment;
        this.time = time;
    }

    public String getUrlavatar() {
        return urlavatar;
    }

    public String getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }
}
