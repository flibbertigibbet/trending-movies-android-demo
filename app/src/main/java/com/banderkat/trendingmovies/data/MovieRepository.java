package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieQueryResponse;
import com.banderkat.trendingmovies.data.networkresource.MovieNetworkBoundResource;
import com.banderkat.trendingmovies.data.networkresource.Resource;

import java.util.List;

import javax.inject.Inject;

public class MovieRepository {

    private static final String LOG_LABEL = "MovieRepository";

    private final MovieWebservice webservice;
    private final MovieDao movieDao;
    private final Context context;

    @Inject
    public MovieRepository(MovieWebservice webservice, MovieDao movieDao, Context context) {
        this.webservice = webservice;
        this.movieDao = movieDao;
        this.context = context;
    }

    public LiveData<Movie> getMovie(long movieId) {
        // return a LiveData item directly from the database.
        return movieDao.getMovie(movieId);
    }

    public LiveData<List<Movie>> getMovies() {
        return movieDao.getPopularMovies();
    }

    public LiveData<Resource<List<Movie>>> loadMovies() {
        return new MovieNetworkBoundResource(webservice, movieDao, context) {
            @NonNull
            @Override
            protected LiveData<List<Movie>> loadFromDb() {
                return movieDao.getPopularMovies();
            }
        }.getAsLiveData();
    }
}
