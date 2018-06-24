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
import com.banderkat.trendingmovies.data.models.MovieVideo;
import com.banderkat.trendingmovies.di.MovieViewModelFactory;
import com.banderkat.trendingmovies.trendingmovies.R;
import com.banderkat.trendingmovies.trendingmovies.databinding.ActivityMovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static final DateFormat releaseDateFormat, displayFormat;

    static {
        releaseDateFormat = new SimpleDateFormat("y-M-d", US);
        displayFormat = new SimpleDateFormat("MMMM d, y", US);
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
        viewModel.getMovieInfo(movieId).observe(this, movieInfo -> {
            if (movieInfo == null || movieInfo.getMovie() == null) {
                Log.e(LOG_LABEL, "Could not find movie to show detail for ID: " + movieId);
                return;
            }

            this.movie = movieInfo.getMovie();
            Log.d(LOG_LABEL, "Loaded data for movie " + movie.getTitle());

            if (movie.gotVideos()) {
                Log.d(LOG_LABEL, "Found " + movieInfo.getVideos().size() + " videos for movie in info");
                for (MovieVideo video : movieInfo.getVideos()) {
                    Log.d(LOG_LABEL, "Video: " + video.getName());
                }
            } else {
                Log.w(LOG_LABEL, "Have no videos yet for movie");
                viewModel.loadMovieVideos(movieId).observe(this, response -> {
                    if (response == null || response.data == null) {
                        Log.w(LOG_LABEL, "no videos found yet for movie...");
                        return;
                    }
                    // outside observer should refresh itself, so nothing to do here
                    Log.d(LOG_LABEL, "Loaded videos for movie.");
                    viewModel.loadMovieVideos(movieId).removeObservers(this);
                });
            }

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

    public String getReleaseDisplayDate() {
        if (movie == null) {
            return "";
        }

        try {
            Date releaseDate = releaseDateFormat.parse(movie.getReleaseDate());
            return displayFormat.format(releaseDate);
        } catch (ParseException e) {
            Log.e(LOG_LABEL, "Could not parse release year");
            e.printStackTrace();
        }

        return "";
    }
}
