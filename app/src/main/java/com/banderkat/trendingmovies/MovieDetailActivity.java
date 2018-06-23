package com.banderkat.trendingmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.banderkat.trendingmovies.data.MovieViewModel;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.di.MovieViewModelFactory;
import com.banderkat.trendingmovies.trendingmovies.R;

import javax.inject.Inject;

public class MovieDetailActivity extends AppCompatActivity {
    @SuppressWarnings("WeakerAccess")
    @Inject
    MovieViewModelFactory viewModelFactory;
    @SuppressWarnings("WeakerAccess")
    MovieViewModel viewModel;

    private static final String LOG_LABEL = "MovieDetail";

    public static final String MOVIE_ID_DETAIL_KEY = "movie_id";

    private long movieId;
    private Movie movie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieViewModel.class);

        Log.d(LOG_LABEL, "onCreate movie detail");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MOVIE_ID_DETAIL_KEY)) {
            movieId =intent.getLongExtra(MOVIE_ID_DETAIL_KEY, -1);
            if (movieId < 0) {
                Log.e(LOG_LABEL, "Failed to get movie ID for detail view");
            } else {
                loadMovieData();
            }
        }
    }

    private void loadMovieData() {
        Log.d(LOG_LABEL, "loading movie details for ID " + movieId);
        viewModel.getMovie(movieId).observe(this, movie -> {
            if (movie == null) {
                Log.e(LOG_LABEL, "Could not find movie to show detail for ID: " + movieId);
                return;
            }

            Log.d(LOG_LABEL, "Loaded data for movie " + movie.getTitle());
            this.movie = movie;
            // TODO: set up data binding
        });
    }
}
