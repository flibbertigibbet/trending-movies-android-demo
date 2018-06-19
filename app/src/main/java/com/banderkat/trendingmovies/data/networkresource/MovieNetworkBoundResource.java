package com.banderkat.trendingmovies.data.networkresource;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
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
    private static final long RATE_LIMIT = TimeUnit.MINUTES.toMillis(15);

    public String apiKey;

    // if true, query is for trending, if false, query is for most popular
    private boolean isTrending;

    public MovieDao movieDao;
    public MovieWebservice movieWebservice;

    public MovieNetworkBoundResource(MovieDao movieDao, MovieWebservice movieWebservice, String apiKey) {
        super();
        this.movieDao = movieDao;
        this.movieWebservice = movieWebservice;
        this.apiKey = apiKey;

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
            if (movie.isAdult()) continue;
            movie.setTimestamp(timestamp);

            // track pagination and order from the results on each movie
            if (isTrending) {
                movie.setTrendingPage(pageNum);
                movie.setTrendingPageOrder(i);
            } else {
                movie.setPopularPage(pageNum);
                movie.setPopularPageOrder(i);
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
        long pageNum = isTrending ? first.getTrendingPage() : first.getPopularPage();
        pageNum++;
        if (pageNum < FIRST_PAGE) {
            pageNum = FIRST_PAGE;
        }
        boolean expired = System.currentTimeMillis() - first.getTimestamp() > RATE_LIMIT;
        if (expired) {

            movieDao.clear(); // throw out the full cache
            return (int)pageNum;
        } else {
            return -1; // flag to not fetch
        }
    }

    @NonNull
    @Override
    protected LiveData<PagedList<Movie>> loadFromDb(int pageNumber) {
        DataSource.Factory<Integer, Movie> factory = movieDao.getPopularMovies(pageNumber);

        if (factory != null) {
            Log.d(LOG_LABEL, "Have factory");
            DataSource<Integer, Movie> source = factory.create();
        } else {
            Log.e(LOG_LABEL, "No factory!");
        }

        return new LivePagedListBuilder<>(movieDao.getPopularMovies(pageNumber), MovieRepository.PAGE_SIZE)
                .setBoundaryCallback(new PagedList.BoundaryCallback<Movie>() {

            @Override
            public void onZeroItemsLoaded() {
                super.onZeroItemsLoaded();
                movieDao.getPopularMovies(pageNumber);
            }

            @Override
            public void onItemAtFrontLoaded(@NonNull Movie itemAtFront) {
                super.onItemAtFrontLoaded(itemAtFront);
            }

            @Override
            public void onItemAtEndLoaded(@NonNull Movie itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
                movieDao.getPopularMovies(pageNumber);
            }
        }).setInitialLoadKey(1).build();
    }

    @NonNull
    @Override
    protected LiveData<ApiResponse<MovieQueryResponse>> createCall(int pageNum) {
        return movieWebservice.getPopularMovies(apiKey, (long)pageNum);
    }

    public void setIsTrending(boolean isTrending) {
        this.isTrending = isTrending;
    }
}
