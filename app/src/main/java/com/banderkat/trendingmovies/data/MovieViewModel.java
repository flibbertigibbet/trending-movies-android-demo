package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.ViewModel;

public class MovieViewModel extends ViewModel {
    protected final MovieRepository movieRepository;

    MovieViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
}
