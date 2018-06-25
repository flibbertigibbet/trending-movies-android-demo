package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.util.Log;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieFlag;
import com.banderkat.trendingmovies.data.models.MovieInfo;
import com.banderkat.trendingmovies.data.models.MovieReview;
import com.banderkat.trendingmovies.data.models.MovieVideo;
import com.banderkat.trendingmovies.data.networkresource.Resource;

import java.util.List;

import javax.inject.Inject;

public class MovieViewModel extends ViewModel {

    private static final String LOG_LABEL = "ViewModel";

    public final MovieRepository movieRepository;

    @Inject
    public MovieViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isMostPopular, boolean onlyFavorites) {
        Log.d(LOG_LABEL, "loading movies in view model. is most popular: " + isMostPopular);
        return movieRepository.loadMovies(isMostPopular, onlyFavorites);
    }

    public LiveData<Resource<List<MovieVideo>>> loadMovieVideos(long movieId) {
        Log.d(LOG_LABEL, "loading movie videos in view model for movie " + movieId);
        return movieRepository.loadMovieVideos(movieId);
    }

    public LiveData<Resource<List<MovieReview>>> loadMovieReviews(long movieId) {
        Log.d(LOG_LABEL, "loading movie reviews in view model for movie " + movieId);
        return movieRepository.loadMovieReviews(movieId);
    }

    public LiveData<Movie> getMovie(long movieId) {
        Log.d(LOG_LABEL, "getting movie in view model: " + movieId);
        return movieRepository.getMovie(movieId);
    }

    public LiveData<MovieInfo> getMovieInfo(long movieId) {
        Log.d(LOG_LABEL, "getting movie with additional info: " + movieId);
        return movieRepository.getMovieInfo(movieId);
    }

    public void setMovieFlag(MovieFlag flag) {
        movieRepository.setFlag(flag);
    }
}
