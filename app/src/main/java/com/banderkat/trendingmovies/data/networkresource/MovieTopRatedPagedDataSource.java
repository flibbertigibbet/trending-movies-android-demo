package com.banderkat.trendingmovies.data.networkresource;

import android.support.annotation.NonNull;

import com.banderkat.trendingmovies.data.models.Movie;

public class MovieTopRatedPagedDataSource extends MoviePagedDataSource {

    @NonNull
    @Override
    public Integer getKey(@NonNull Movie item) {
        return (int)item.getTopRatedPage();
    }
}
