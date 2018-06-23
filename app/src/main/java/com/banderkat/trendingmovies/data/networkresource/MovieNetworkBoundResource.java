package com.banderkat.trendingmovies.data.networkresource;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.banderkat.trendingmovies.data.MovieDao;
import com.banderkat.trendingmovies.data.MovieRepository;
import com.banderkat.trendingmovies.data.MovieWebservice;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieQueryResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class MovieNetworkBoundResource extends NetworkBoundResource<PagedList<Movie>, MovieQueryResponse> {

    private static final String LOG_LABEL = "MovieNetworkResource";

    // maximum rate at which to refresh data from network
    private static final long RATE_LIMIT = TimeUnit.HOURS.toMillis(1);

    public String apiKey;

    private boolean isMostPopular;

    public MovieDao movieDao;
    public MovieWebservice movieWebservice;

    public MovieNetworkBoundResource(MovieDao movieDao, MovieWebservice movieWebservice, String apiKey, boolean isMostPopular) {
        super();
        this.movieDao = movieDao;
        this.movieWebservice = movieWebservice;
        this.apiKey = apiKey;
        this.isMostPopular = isMostPopular;

        setupSource();
    }

    @Override
    protected void saveCallResult(@NonNull MovieQueryResponse item) {
        long pageNum = item.getPage();
        if (pageNum != item.getPage()) {
            Log.e(LOG_LABEL, "Requested page " + pageNum + " but got " + item.getPage());
            return;
        }
        Long timestamp = System.currentTimeMillis();
        List<Movie> movies = item.getResults();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            movie.setTimestamp(timestamp);

            // track pagination and order from the results on each movie
            if (isMostPopular) {
                movie.setPopularPage(pageNum);
                movie.setPopularPageOrder(i);
            } else {
                movie.setTopRatedPage(pageNum);
                movie.setTopRatedPageOrder(i);
            }
            movieDao.save(movie);
        }
    }

    @Override
    protected int pageToFetch(@Nullable PagedList<Movie> data) {
        if (data == null || data.isEmpty()) {
            return FIRST_PAGE;
        }

        Movie first = data.get(0);
        long pageNum = isMostPopular ? first.getPopularPage() : first.getTopRatedPage();
        if (pageNum < FIRST_PAGE) {
            pageNum = FIRST_PAGE;
        }
        boolean expired = (System.currentTimeMillis() - first.getTimestamp()) > RATE_LIMIT;
        if (expired) {
            Log.d(LOG_LABEL, "Clearing cache in database");
            Log.d(LOG_LABEL, "last timestamp: " + first.getTimestamp());
            Log.d(LOG_LABEL, "current timestamp: " + System.currentTimeMillis());
            Log.d(LOG_LABEL, "rate limit: " + RATE_LIMIT);
            movieDao.clear(); // throw out the full cache
            return (int)pageNum;
        } else {
            Log.d(LOG_LABEL, "Use database cache");
            return -1; // flag to not fetch
        }
    }

    @NonNull
    @Override
    protected LiveData<PagedList<Movie>> loadFromDb(int pageNumber) {

        DataSource.Factory<Integer, Movie> factory;
        if (isMostPopular) {
            Log.d(LOG_LABEL, "Factory for popular movies");
            factory = movieDao.getPopularMovies(pageNumber);
        } else {
            Log.d(LOG_LABEL, "Factory for top rated movies");
            factory = movieDao.getTopRatedMovies(pageNumber);
        }

        return new LivePagedListBuilder<>(factory, MovieRepository.PAGE_SIZE)
                .setBoundaryCallback(new PagedList.BoundaryCallback<Movie>() {

            @Override
            public void onZeroItemsLoaded() {
                super.onZeroItemsLoaded();
                if (isMostPopular) {
                    movieDao.getPopularMovies(pageNumber);
                } else {
                    movieDao.getTopRatedMovies(pageNumber);
                }

            }

            @Override
            public void onItemAtFrontLoaded(@NonNull Movie itemAtFront) {
                super.onItemAtFrontLoaded(itemAtFront);
            }

            @Override
            public void onItemAtEndLoaded(@NonNull Movie itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
                if (isMostPopular) {
                    movieDao.getPopularMovies(pageNumber);
                } else {
                    movieDao.getTopRatedMovies(pageNumber);
                }

            }
        }).setInitialLoadKey(1).build();
    }

    @NonNull
    @Override
    protected LiveData<ApiResponse<MovieQueryResponse>> createCall(int pageNum) {
        if (isMostPopular) {
            return movieWebservice.getPopularMovies(apiKey, (long)pageNum);
        } else {
            return movieWebservice.getTopRatedMovies(apiKey, (long)pageNum);
        }

    }
}
