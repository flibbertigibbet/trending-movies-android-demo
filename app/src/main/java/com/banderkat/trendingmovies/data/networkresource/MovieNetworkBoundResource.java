package com.banderkat.trendingmovies.data.networkresource;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.banderkat.trendingmovies.data.MovieDao;
import com.banderkat.trendingmovies.data.MovieWebservice;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieQueryResponse;
import com.banderkat.trendingmovies.trendingmovies.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MovieNetworkBoundResource extends NetworkBoundResource<List<Movie>, MovieQueryResponse> {

    // maximum rate at which to refresh data from network
    private static final long RATE_LIMIT = TimeUnit.MINUTES.toMillis(15);

    private final MovieWebservice webservice;
    private final MovieDao movieDao;
    private final String apiKey;

    public MovieNetworkBoundResource(MovieWebservice webservice, MovieDao movieDao, Context context) {
        this.webservice = webservice;
        this.movieDao = movieDao;
        this.apiKey = context.getString(R.string.api_key);
    }

    @Override
    protected void saveCallResult(@NonNull MovieQueryResponse item) {
        movieDao.clear();
        Long timestamp = System.currentTimeMillis();
        List<Movie> movies = item.getResults();
        for (Movie movie: movies) {
            movie.setTimestamp(timestamp);
            movieDao.save(movie);
        }
    }

    @Override
    protected boolean shouldFetch(@Nullable List<Movie> data) {
        if (data == null || data.isEmpty()) {
            return true;
        }
        Movie first = data.get(0);
        return System.currentTimeMillis() - first.getTimestamp() > RATE_LIMIT;
    }

    @NonNull
    @Override
    protected LiveData<List<Movie>> loadFromDb() {
        return movieDao.getPopularMovies();
    }

    @NonNull
    @Override
    protected LiveData<ApiResponse<MovieQueryResponse>> createCall() {
        return webservice.getPopularMovies(apiKey, 1);
    }
}
