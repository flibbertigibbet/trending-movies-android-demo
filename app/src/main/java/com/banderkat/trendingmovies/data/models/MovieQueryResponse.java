package com.banderkat.trendingmovies.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Gson model for the query response used for lists of movies.
 */


public class MovieQueryResponse {

    private final long page;

    @SerializedName("toal_results")
    private final long totalResults;

    @SerializedName("total_pages")
    private final long totalPages;

    private final List<Movie> results;


    public MovieQueryResponse(long page, long totalResults, long totalPages, List<Movie>results) {
        this.page = page;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.results = results;
    }

    public long getPage() {
        return page;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public List<Movie> getResults() {
        return results;
    }
}
