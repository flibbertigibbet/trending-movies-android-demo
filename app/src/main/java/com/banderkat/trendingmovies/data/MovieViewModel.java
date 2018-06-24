package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.util.Log;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieInfo;
import com.banderkat.trendingmovies.data.models.MovieVideo;
import com.banderkat.trendingmovies.data.networkresource.Resource;
import com.banderkat.trendingmovies.trendingmovies.R;

import java.util.List;

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

    public LiveData<Resource<List<MovieVideo>>> loadMovieVideos(long movieId) {
        Log.d(LOG_LABEL, "loading movie videos in view model for movie " + movieId);
        return movieRepository.loadMovieVideos(movieId);
    }

    public LiveData<Movie> getMovie(long movieId) {
        Log.d(LOG_LABEL, "getting movie in view model: " + movieId);
        return movieRepository.getMovie(movieId);
    }

    public LiveData<MovieInfo> getMovieInfo(long movieId) {
        Log.d(LOG_LABEL, "getting movie with additional info: " + movieId);
        return movieRepository.getMovieInfo(movieId);
    }
}
