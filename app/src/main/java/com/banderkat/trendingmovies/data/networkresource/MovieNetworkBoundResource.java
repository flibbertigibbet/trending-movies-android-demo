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
import com.banderkat.trendingmovies.data.ReviewDao;
import com.banderkat.trendingmovies.data.VideoDao;
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
    public VideoDao videoDao;
    public ReviewDao reviewDao;
    public MovieWebservice movieWebservice;

    public MovieNetworkBoundResource(MovieDao movieDao, VideoDao videoDao, ReviewDao reviewDao,
                                     MovieWebservice movieWebservice,
                                     String apiKey, boolean isMostPopular) {
        super();
        this.movieDao = movieDao;
        this.videoDao = videoDao;
        this.reviewDao = reviewDao;
        this.movieWebservice = movieWebservice;
        this.apiKey = apiKey;
        this.isMostPopular = isMostPopular;

        setupSource(FIRST_PAGE);
    }

    @Override
    protected void saveCallResult(@NonNull MovieQueryResponse item) {
        long pageNum = item.getPage();
        if (pageNum != item.getPage()) {
            Log.e(LOG_LABEL, "Requested page " + pageNum + " but got " + item.getPage());
            return;
        }

        List<Movie> movies = item.getResults();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            movie.setTimestamp(System.currentTimeMillis());

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
    protected boolean fetchPageFromNetwork(@Nullable PagedList<Movie> data, int pageNumber) {
        if (data == null || data.isEmpty()) {
            return true;
        }

        Movie first = data.get(0);
        boolean expired = (System.currentTimeMillis() - first.getTimestamp()) > RATE_LIMIT;
        if (expired) {
            Log.d(LOG_LABEL, "Clearing cache in database");
            Log.d(LOG_LABEL, "last timestamp: " + first.getTimestamp());
            Log.d(LOG_LABEL, "current timestamp: " + System.currentTimeMillis());
            Log.d(LOG_LABEL, "rate limit: " + RATE_LIMIT);
            movieDao.clear(); // throw out the full cache
            videoDao.clear();
            reviewDao.clear();
            return true;
        } else {
            long lastPage = isMostPopular ? first.getPopularPage() : first.getTopRatedPage();
            if (lastPage >= pageNumber) {
                Log.d(LOG_LABEL, "Use database cache");
                return false;
            }
            return true;
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

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(MovieRepository.PAGE_SIZE)
                .setPrefetchDistance(4).build();

        return new LivePagedListBuilder<>(factory, config).setBoundaryCallback(new PagedList.BoundaryCallback<Movie>() {

            @Override
            public void onZeroItemsLoaded() {
                super.onZeroItemsLoaded();
                Log.w(LOG_LABEL, "onZeroItemsLoaded");
            }

            @Override
            public void onItemAtFrontLoaded(@NonNull Movie itemAtFront) {
                super.onItemAtFrontLoaded(itemAtFront);
                Log.d(LOG_LABEL, "onItemAtFrontLoaded");
            }

            @Override
            public void onItemAtEndLoaded(@NonNull Movie itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
                Log.d(LOG_LABEL, "onItemAtEndLoaded");
                int lastPage;
                if (isMostPopular) {
                    lastPage = (int)itemAtEnd.getPopularPage();
                } else {
                    lastPage = (int)itemAtEnd.getTopRatedPage();
                }

                if (lastPage < FIRST_PAGE) {
                    Log.e(LOG_LABEL, "Failed to find last loaded page to load next");
                    return;
                }

                setupSource(lastPage + 1);

            }
        }).setInitialLoadKey(1).build();
    }

    @NonNull
    @Override
    protected LiveData<ApiResponse<MovieQueryResponse>> createCall(int pageNum) {
        Log.d(LOG_LABEL, "createCall for page number " + pageNum);
        if (isMostPopular) {
            return movieWebservice.getPopularMovies(apiKey, (long)pageNum);
        } else {
            return movieWebservice.getTopRatedMovies(apiKey, (long)pageNum);
        }

    }
}
