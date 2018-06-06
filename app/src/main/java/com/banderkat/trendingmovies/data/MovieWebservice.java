package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Use Retrofit to query the server API.
 */

public interface MovieWebservice {

    // base URL; should end in a trailing slash
    String WEBSERVICE_URL = "https://example.org/";


    @GET("popular")
    LiveData<ApiResponse<List<Movie>>> getPopularMovies();

}
