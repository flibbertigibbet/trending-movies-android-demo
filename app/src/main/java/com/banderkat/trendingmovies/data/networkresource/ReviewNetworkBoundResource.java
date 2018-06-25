package com.banderkat.trendingmovies.data.networkresource;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.banderkat.trendingmovies.data.MovieWebservice;
import com.banderkat.trendingmovies.data.ReviewDao;
import com.banderkat.trendingmovies.data.models.MovieReview;
import com.banderkat.trendingmovies.data.models.MovieReviewsQueryResponse;

import java.util.List;

public class ReviewNetworkBoundResource extends NetworkBoundResource<List<MovieReview>, MovieReviewsQueryResponse> {

    private static final String LOG_LABEL = "ReviewNetworkResource";

    public final String apiKey;
    public final ReviewDao reviewDao;
    public final MovieWebservice movieWebservice;

    private final long movieId;

    public ReviewNetworkBoundResource(ReviewDao reviewDao, MovieWebservice movieWebservice, String apiKey, long movieId) {
        this.reviewDao = reviewDao;
        this.apiKey = apiKey;
        this.movieWebservice = movieWebservice;
        this.movieId = movieId;
        setupSource(FIRST_PAGE);
    }


    @Override
    protected void saveCallResult(@NonNull MovieReviewsQueryResponse item) {
        reviewDao.saveReviews(movieId, item.getResults());
    }

    @Override
    protected boolean fetchPageFromNetwork(@Nullable List<MovieReview> data, int pageNumber) {
        return (data == null || data.isEmpty());
    }

    @NonNull
    @Override
    protected LiveData<List<MovieReview>> loadFromDb(int pageNumber) {
        return reviewDao.getMovieReviews(movieId);
    }

    @NonNull
    @Override
    protected LiveData<ApiResponse<MovieReviewsQueryResponse>> createCall(int pageNumber) {
        return movieWebservice.getMovieReviews(movieId, apiKey);
    }
}
