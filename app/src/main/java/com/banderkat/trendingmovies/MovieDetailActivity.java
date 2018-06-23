package com.banderkat.trendingmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.banderkat.trendingmovies.data.MovieViewModel;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.di.MovieViewModelFactory;
import com.banderkat.trendingmovies.trendingmovies.R;
import com.banderkat.trendingmovies.trendingmovies.databinding.ActivityMovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import static java.util.Locale.US;

public class MovieDetailActivity extends AppCompatActivity {
    @SuppressWarnings("WeakerAccess")
    @Inject
    MovieViewModelFactory viewModelFactory;
    @SuppressWarnings("WeakerAccess")
    MovieViewModel viewModel;

    public static final String MOVIE_ID_DETAIL_KEY = "movie_id";

    private static final String LOG_LABEL = "MovieDetail";
    private static final DateFormat releaseDateFormat;

    static {
        releaseDateFormat = new SimpleDateFormat("y-M-d", US);
    }

    private long movieId;
    private Movie movie;
    private ActivityMovieDetailBinding binding;

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

            binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
            binding.setMovie(movie);
            binding.setActivity(this);

            ActionBar actionBar = getSupportActionBar();

            if (actionBar == null) {
                Log.e(LOG_LABEL, "could not find detail action bar");
                return;
            }

            actionBar.setTitle(movie.getTitle());
            actionBar.setDisplayHomeAsUpEnabled(true);

            Picasso.with(this)
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.ic_image_placeholder_white_185x277dp)
                    .fit()
                    .into(binding.movieDetailPoster);
        });
    }

    public String getReleaseYear() {
        if (movie == null) {
            return "";
        }

        try {
            Date releaseDate = releaseDateFormat.parse(movie.getReleaseDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(releaseDate);
            return String.valueOf(calendar.get(Calendar.YEAR));
        } catch (ParseException e) {
            Log.e(LOG_LABEL, "Could not parse release year");
            e.printStackTrace();
        }

        return "";
    }
}
