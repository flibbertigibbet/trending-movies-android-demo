package com.banderkat.trendingmovies.data.networkresource;

import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.banderkat.trendingmovies.data.models.Movie;

abstract public class MoviePagedDataSource extends ItemKeyedDataSource<Integer, Movie> {

    private static final String LOG_LABEL = "PagedDataSource";

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {
        Log.d(LOG_LABEL, "loadInitial");
    }

    @Override
    public void loadBefore(@NonNull LoadParams params, @NonNull LoadCallback callback) {
        Log.d(LOG_LABEL, "loadBefore");
    }

    @Override
    public void loadAfter(@NonNull LoadParams params, @NonNull LoadCallback callback) {
        Log.d(LOG_LABEL, "loadAfter");
    }
}
