package com.banderkat.trendingmovies.data.networkresource;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.banderkat.trendingmovies.data.MovieDao;
import com.banderkat.trendingmovies.data.MovieWebservice;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieQueryResponse;
import com.banderkat.trendingmovies.trendingmovies.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MovieNetworkBoundResource extends NetworkBoundResource<PagedList<Movie>, MovieQueryResponse> {

    private static final String LOG_LABEL = "MovieNetworkResource";

    // maximum rate at which to refresh data from network
    private static final long RATE_LIMIT = TimeUnit.MINUTES.toMillis(15);

    private final MovieWebservice webservice;
    private final MovieDao movieDao;
    private final String apiKey;

    // if true, query is for trending, if false, query is for most popular
    private final boolean isTrending;

    public MovieNetworkBoundResource(MovieWebservice webservice, MovieDao movieDao, Context context, boolean isTrending) {
        this.webservice = webservice;
        this.movieDao = movieDao;
        this.apiKey = context.getString(R.string.api_key);
        this.isTrending = isTrending;
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
    protected long pageToFetch(@Nullable PagedList<Movie> data) {
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
            return pageNum;
        } else {
            return -1; // flag to not fetch
        }
    }

    @NonNull
    @Override
    protected LiveData<PagedList<Movie>> loadFromDb() {
        return new LivePagedListBuilder<>(movieDao.getPopularMovies(), 20).setBoundaryCallback(new PagedList.BoundaryCallback<Movie>() {

            @Override
            public void onZeroItemsLoaded() {
                super.onZeroItemsLoaded();
                movieDao.getPopularMovies();
            }

            @Override
            public void onItemAtFrontLoaded(@NonNull Movie itemAtFront) {
                super.onItemAtFrontLoaded(itemAtFront);
            }

            @Override
            public void onItemAtEndLoaded(@NonNull Movie itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
                movieDao.getPopularMovies();
            }
        }).setInitialLoadKey(1L).build();
    }

    @NonNull
    @Override
    protected LiveData<ApiResponse<MovieQueryResponse>> createCall(long pageNum) {
        return webservice.getPopularMovies(apiKey, pageNum);
    }
}
