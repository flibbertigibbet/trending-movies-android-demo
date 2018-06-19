package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.MovieNetworkBoundResource;
import com.banderkat.trendingmovies.data.networkresource.Resource;

import javax.inject.Inject;

public class MovieRepository {

    private static final String LOG_LABEL = "MovieRepository";

    public static final int PAGE_SIZE = 20;

    public final MovieWebservice movieWebservice;
    public final MovieDao movieDao;
    public final String apiKey;

    @Inject
    public MovieRepository(MovieWebservice movieWebservice, MovieDao movieDao, String apiKey) {
        this.movieWebservice = movieWebservice;
        this.movieDao = movieDao;
        this.apiKey = apiKey;
    }

    public LiveData<Movie> getMovie(long movieId) {
        // return a LiveData item directly from the database.
        return movieDao.getMovie(movieId);
    }

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isTrending) {
        MovieNetworkBoundResource resource = new MovieNetworkBoundResource(movieDao, movieWebservice, apiKey);
        resource.setIsTrending(isTrending);
        return resource.getAsLiveData();
    }
}
