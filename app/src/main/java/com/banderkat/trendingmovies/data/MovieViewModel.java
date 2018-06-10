package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.util.Log;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.Resource;

import javax.inject.Inject;

public class MovieViewModel extends ViewModel {

    private static final String LOG_LABEL = "ViewModel";

    public final MovieRepository movieRepository;
    public final MovieDao movieDao;

    @Inject
    MovieViewModel(MovieRepository movieRepository, MovieDao movieDao) {
        this.movieRepository = movieRepository;
        this.movieDao = movieDao;
    }

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isTrending) {
        Log.d(LOG_LABEL, "loading movies in view model");

        if (movieDao != null) {
            Log.d(LOG_LABEL, "have DAO");
        } else {
            Log.e(LOG_LABEL, "Have no DAO");
        }

        return movieRepository.loadMovies(isTrending);
    }
}
