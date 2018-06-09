package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;

import com.banderkat.trendingmovies.data.models.Movie;
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

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isTrending) {
        long pageNumber = 1;
        return new MovieNetworkBoundResource(webservice, movieDao, context, isTrending, pageNumber) {
            @NonNull
            @Override
            protected LiveData<PagedList<Movie>> loadFromDb() {
                if (isTrending) {
                    return movieDao.getTrendingMovies(pageNumber);
                } else {
                    return movieDao.getPopularMovies(pageNumber);
                }
            }
        }.getAsLiveData();
    }
}
