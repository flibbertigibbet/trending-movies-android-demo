package com.banderkat.trendingmovies.data.networkresource;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.banderkat.trendingmovies.data.models.Movie;

public class MoviePagedDataSource extends PageKeyedDataSource<Integer, Movie> {
    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {

    }

    @Override
    public void loadBefore(@NonNull LoadParams params, @NonNull LoadCallback callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams params, @NonNull LoadCallback callback) {

    }


}
