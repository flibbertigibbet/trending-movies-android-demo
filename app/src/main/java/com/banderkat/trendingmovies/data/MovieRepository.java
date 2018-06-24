package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieInfo;
import com.banderkat.trendingmovies.data.models.MovieVideo;
import com.banderkat.trendingmovies.data.networkresource.MovieNetworkBoundResource;
import com.banderkat.trendingmovies.data.networkresource.Resource;
import com.banderkat.trendingmovies.data.networkresource.VideoNetworkBoundResource;

import java.util.List;

import javax.inject.Inject;

public class MovieRepository {

    private static final String LOG_LABEL = "MovieRepository";

    public static final int PAGE_SIZE = 20;

    public final MovieWebservice movieWebservice;
    public final MovieDao movieDao;
    public final VideoDao videoDao;
    public final String apiKey;

    @Inject
    public MovieRepository(MovieWebservice movieWebservice, MovieDao movieDao, VideoDao videoDao, String apiKey) {
        this.movieWebservice = movieWebservice;
        this.movieDao = movieDao;
        this.videoDao = videoDao;
        this.apiKey = apiKey;
    }

    public LiveData<Movie> getMovie(long movieId) {
        // return a LiveData item directly from the database.
        return movieDao.getMovie(movieId);
    }

    public LiveData<MovieInfo> getMovieInfo(long movieId) {
        return movieDao.getMovieInfo(movieId);
    }

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isMostPopular) {
        return new MovieNetworkBoundResource(movieDao, movieWebservice, apiKey, isMostPopular)
                .getAsLiveData();
    }

    public LiveData<Resource<List<MovieVideo>>> loadMovieVideos(long movieId) {
        return new VideoNetworkBoundResource(videoDao, movieWebservice, apiKey, movieId)
                .getAsLiveData();
    }
}
