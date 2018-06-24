package com.banderkat.trendingmovies.data.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class MovieInfo {

    @Embedded
    private final Movie movie;

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = MovieVideo.class)
    private List<MovieVideo> videos;

    @Relation(parentColumn = "id", entityColumn = "movie_id", entity = MovieReview.class)
    private List<MovieReview> reviews;

    public MovieInfo(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public List<MovieVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<MovieVideo> videos) {
        this.videos = videos;
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
    }
}
