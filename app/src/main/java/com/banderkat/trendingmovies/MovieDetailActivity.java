package com.banderkat.trendingmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.banderkat.trendingmovies.data.MovieViewModel;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieViewModel.class);

        Log.d(LOG_LABEL, "onCreate movie detail");
    }
}
