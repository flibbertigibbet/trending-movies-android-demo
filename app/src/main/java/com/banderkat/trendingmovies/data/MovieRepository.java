package com.banderkat.trendingmovies.data;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.os.AsyncTask;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieFlag;
import com.banderkat.trendingmovies.data.models.MovieInfo;
import com.banderkat.trendingmovies.data.models.MovieReview;
import com.banderkat.trendingmovies.data.models.MovieVideo;
import com.banderkat.trendingmovies.data.networkresource.MovieNetworkBoundResource;
import com.banderkat.trendingmovies.data.networkresource.Resource;
import com.banderkat.trendingmovies.data.networkresource.ReviewNetworkBoundResource;
import com.banderkat.trendingmovies.data.networkresource.VideoNetworkBoundResource;

import java.util.List;

import javax.inject.Inject;

public class MovieRepository {

    private static final String LOG_LABEL = "MovieRepository";

    public static final int PAGE_SIZE = 20;

    public final MovieWebservice movieWebservice;
    public final MovieDao movieDao;
    public final ReviewDao reviewDao;
    public final VideoDao videoDao;
    public final FlagDao flagDao;
    public final String apiKey;

    @Inject
    public MovieRepository(MovieWebservice movieWebservice, MovieDao movieDao, ReviewDao reviewDao,
                           VideoDao videoDao, FlagDao flagDao, String apiKey) {
        this.movieWebservice = movieWebservice;
        this.movieDao = movieDao;
        this.reviewDao = reviewDao;
        this.videoDao = videoDao;
        this.flagDao = flagDao;
        this.apiKey = apiKey;
    }

    public LiveData<Movie> getMovie(long movieId) {
        // return a LiveData item directly from the database.
        return movieDao.getMovie(movieId);
    }

    public LiveData<MovieInfo> getMovieInfo(long movieId) {
        return movieDao.getMovieInfo(movieId);
    }

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isMostPopular, boolean onlyFavorites) {
        return new MovieNetworkBoundResource(movieDao, videoDao, reviewDao, movieWebservice, apiKey, isMostPopular, onlyFavorites)
                .getAsLiveData();
    }

    public LiveData<Resource<List<MovieVideo>>> loadMovieVideos(long movieId) {
        return new VideoNetworkBoundResource(videoDao, movieWebservice, apiKey, movieId)
                .getAsLiveData();
    }

    public LiveData<Resource<List<MovieReview>>> loadMovieReviews(long movieId) {
        return new ReviewNetworkBoundResource(reviewDao, movieWebservice, apiKey, movieId)
                .getAsLiveData();
    }

    @SuppressLint("StaticFieldLeak")
    public void setFlag(MovieFlag flag) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                flagDao.save(flag);
                return null;
            }
        }.execute();
    }
}
