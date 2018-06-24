package com.banderkat.trendingmovies.data.networkresource;

import android.support.annotation.NonNull;

import com.banderkat.trendingmovies.data.models.Movie;

public class MoviePopularPagedDataSource extends MoviePagedDataSource {

    private static final String LOG_LABEL = "PopularDataSource";

    @NonNull
    @Override
    public Integer getKey(@NonNull Movie item) {
        return (int)item.getPopularPage();
    }

}
