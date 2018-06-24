package com.banderkat.trendingmovies.data.models;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieReviewsQueryResponse {

    private final long id;
    private final long page;

    @SerializedName("total_pages")
    @ColumnInfo(name = "total_pages")
    private final long totalPages;

    @SerializedName("total_results")
    @ColumnInfo(name = "total_results")
    private final long totalResults;

    private final List<MovieReview> results;


    public MovieReviewsQueryResponse(long id, long page, long totalPages, long totalResults, List<MovieReview> results) {
        this.id = id;
        this.page = page;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
        this.results = results;
    }

    public long getId() {
        return id;
    }

    public long getPage() {
        return page;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public List<MovieReview> getResults() {
        return results;
    }
}
