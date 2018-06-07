package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.Resource;

import java.util.List;

import javax.inject.Inject;

public class MovieViewModel extends ViewModel {
    protected final MovieRepository movieRepository;
    private final LiveData<Resource<List<Movie>>> movies;

    @Inject
    MovieViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        movies = movieRepository.loadMovies();
    }

    public LiveData<Resource<List<Movie>>> loadMovies() {
        return movies;
    }
}
