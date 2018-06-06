package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;

import com.banderkat.trendingmovies.data.models.Movie;

import javax.inject.Inject;

public class MovieRepository {

    private static final String LOG_LABEL = "MovieRepository";

    private final MovieWebservice webservice;
    private final MovieDao movieDao;

    @Inject
    public MovieRepository(MovieWebservice webservice,
                                 MovieDao movieDao) {
        this.webservice = webservice;
        this.movieDao = movieDao;
    }

    public LiveData<Movie> getMovie(long movieId) {
        // return a LiveData item directly from the database.
        return movieDao.getMovie(movieId);
    }
}
