package com.example.moviereview;

public class Review {
    private String title;
    private int score;
    private String comment;

    public Review(String title, int score, String comment) {
        this.title = title;
        this.score = score;
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }
}

