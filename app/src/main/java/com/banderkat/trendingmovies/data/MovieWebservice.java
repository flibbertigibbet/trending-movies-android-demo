package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;

import com.banderkat.trendingmovies.data.models.MovieQueryResponse;
import com.banderkat.trendingmovies.data.models.MovieVideosQueryResponse;
import com.banderkat.trendingmovies.data.networkresource.ApiResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Use Retrofit to query the server API.
 */

public interface MovieWebservice {

    // must end in a trailing slash
    String WEBSERVICE_URL = "https://api.themoviedb.org/3/";

    @GET("movie/popular")
    LiveData<ApiResponse<MovieQueryResponse>> getPopularMovies(@Query("api_key") String api_key,
                                                               @Query("page") Long page);

    @GET("movie/top_rated")
    LiveData<ApiResponse<MovieQueryResponse>> getTopRatedMovies(@Query("api_key") String api_key,
                                                                @Query("page") Long page);

    @GET("movie/{id}/videos")
    LiveData<ApiResponse<MovieVideosQueryResponse>> getMovieVideos(@Path("id") Long id,
                                                                   @Query("api_key") String api_key);

}
