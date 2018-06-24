package com.banderkat.trendingmovies.data.models;

import java.util.List;

public class MovieVideosQueryResponse {

    private final long id; // the ID of the movie

    private final List<MovieVideo> results;

    public MovieVideosQueryResponse(long id, List<MovieVideo> results) {
        this.id = id;
        this.results = results;
    }

    public long getId() {
        return id;
    }

    public List<MovieVideo> getResults() {
        return results;
    }
}
