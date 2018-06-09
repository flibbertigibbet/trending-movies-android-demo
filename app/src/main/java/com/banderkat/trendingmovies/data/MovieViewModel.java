package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.Resource;

import javax.inject.Inject;

public class MovieViewModel extends ViewModel {
    protected final MovieRepository movieRepository;

    @Inject
    MovieViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isTrending) {
        return movieRepository.loadMovies(isTrending);
    }
}
