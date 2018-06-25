package com.banderkat.trendingmovies.data.networkresource;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.banderkat.trendingmovies.data.MovieWebservice;
import com.banderkat.trendingmovies.data.VideoDao;
import com.banderkat.trendingmovies.data.models.MovieVideo;
import com.banderkat.trendingmovies.data.models.MovieVideosQueryResponse;

import java.util.List;

public class VideoNetworkBoundResource extends NetworkBoundResource<List<MovieVideo>, MovieVideosQueryResponse> {

    private static final String LOG_LABEL = "VideoNetworkResource";

    public final String apiKey;
    public final VideoDao videoDao;
    public final MovieWebservice movieWebservice;

    private final long movieId;

    public VideoNetworkBoundResource(VideoDao videoDao, MovieWebservice movieWebservice, String apiKey, long movieId) {
        this.videoDao = videoDao;
        this.apiKey = apiKey;
        this.movieWebservice = movieWebservice;
        this.movieId = movieId;
        setupSource(FIRST_PAGE);
    }

    @Override
    protected void saveCallResult(@NonNull MovieVideosQueryResponse item) {
        videoDao.saveVideos(item.getId(), item.getResults());
    }

    @Override
    protected boolean fetchPageFromNetwork(@Nullable List<MovieVideo> data, int pageNumber) {
        return (data == null || data.isEmpty());
    }

    @NonNull
    @Override
    protected LiveData<List<MovieVideo>> loadFromDb(int pageNumber) {
        return videoDao.getMovieVideos(movieId);
    }

    @NonNull
    @Override
    protected LiveData<ApiResponse<MovieVideosQueryResponse>> createCall(int pageNumber) {
        return movieWebservice.getMovieVideos(movieId, apiKey);
    }
}
