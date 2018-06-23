package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.util.Log;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.Resource;
import com.banderkat.trendingmovies.trendingmovies.R;

import javax.inject.Inject;

public class MovieViewModel extends ViewModel {

    private static final String LOG_LABEL = "ViewModel";

    public final MovieRepository movieRepository;
    public final MovieDao movieDao;

    @Inject
    public MovieViewModel(MovieRepository movieRepository, MovieDao movieDao) {
        if (movieDao != null) {
            Log.d(LOG_LABEL, "view model has DAO");
        } else {
            Log.d(LOG_LABEL, "view model has no DAO!");
        }
        this.movieDao = movieDao;
        this.movieRepository = movieRepository;
    }

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isMostPopular) {
        Log.d(LOG_LABEL, "loading movies in view model. is most popular: " + isMostPopular);
        return movieRepository.loadMovies(isMostPopular);
    }

    public LiveData<Movie> getMovie(long movieId) {
        Log.d(LOG_LABEL, "getting move in view model: " + movieId);
        return movieRepository.getMovie(movieId);
    }
}
