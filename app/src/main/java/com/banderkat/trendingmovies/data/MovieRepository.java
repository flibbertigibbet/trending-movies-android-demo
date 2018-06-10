package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.util.Log;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.MovieNetworkBoundResource;
import com.banderkat.trendingmovies.data.networkresource.Resource;

import javax.inject.Inject;

public class MovieRepository {

    private static final String LOG_LABEL = "MovieRepository";

    public static final int PAGE_SIZE = 20;

    private final MovieWebservice movieWebservice;
    private final MovieDao movieDao;
    private final Context context;

    @Inject
    public MovieRepository(MovieWebservice movieWebservice, MovieDao movieDao, Context context) {
        this.movieWebservice = movieWebservice;
        this.movieDao = movieDao;
        this.context = context;

        if (this.movieDao != null) {
            Log.d("Repository", "Have DAO");
        } else {
            Log.e("Repository", "No DAO");
        }
    }

    public LiveData<Movie> getMovie(long movieId) {
        // return a LiveData item directly from the database.
        return movieDao.getMovie(movieId);
    }

    public LiveData<Resource<PagedList<Movie>>> loadMovies(boolean isTrending) {
        if (movieDao != null) {
            Log.d(LOG_LABEL, "Still have DAO");
        } else {
            Log.d(LOG_LABEL, "Missing DAO");
        }

        // FIXME: crashes here
        MovieNetworkBoundResource resource = new MovieNetworkBoundResource(movieWebservice, movieDao, context, isTrending);

        if (resource != null) {
            Log.d(LOG_LABEL, "Got resource " + resource.getAsLiveData().toString());
        } else {
            Log.e(LOG_LABEL, "Failed to create resource");
        }

        return resource.getAsLiveData();
    }
}
