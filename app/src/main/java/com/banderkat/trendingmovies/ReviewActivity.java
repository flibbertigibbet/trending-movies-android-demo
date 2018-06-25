package com.banderkat.trendingmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.banderkat.trendingmovies.data.MovieViewModel;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieReview;
import com.banderkat.trendingmovies.di.MovieViewModelFactory;
import com.banderkat.trendingmovies.trendingmovies.R;
import com.banderkat.trendingmovies.trendingmovies.databinding.ActivityReviewBinding;

import javax.inject.Inject;

import static com.banderkat.trendingmovies.MovieDetailActivity.MOVIE_ID_DETAIL_KEY;

public class ReviewActivity extends AppCompatActivity {

    private static final String LOG_LABEL = "ReviewActivity";

    public static final String MOVIE_ID_REVIEW_KEY = "movie_review_id";

    @SuppressWarnings("WeakerAccess")
    @Inject
    MovieViewModelFactory viewModelFactory;
    @SuppressWarnings("WeakerAccess")
    MovieViewModel viewModel;

    ActivityReviewBinding binding;

    private long movieId;
    private String reviewId;
    private Movie movie;
    private MovieReview review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) {
            Log.e(LOG_LABEL, "could not find detail action bar");
            return;
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieViewModel.class);

        Log.d(LOG_LABEL, "onCreate movie detail");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MOVIE_ID_DETAIL_KEY) && intent.hasExtra(MOVIE_ID_REVIEW_KEY)) {
            movieId = intent.getLongExtra(MOVIE_ID_DETAIL_KEY, -1);
            reviewId = intent.getStringExtra(MOVIE_ID_REVIEW_KEY);
            if (movieId < 0 || reviewId == null || reviewId.isEmpty()) {
                Log.e(LOG_LABEL, "Failed to get movie ID for detail view");
            } else {
                loadMovieData();
            }
        }
    }

    private void loadMovieData() {
        Log.d(LOG_LABEL, "loading movie details for ID " + movieId);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review);

        viewModel.getMovieInfo(movieId).observe(this, movieInfo -> {
            if (movieInfo == null || movieInfo.getMovie() == null) {
                Log.e(LOG_LABEL, "Could not find movie to show detail for ID: " + movieId);
                return;
            }

            this.movie = movieInfo.getMovie();
            binding.setMovie(movie);

            Log.d(LOG_LABEL, "Loaded data for movie " + movie.getTitle());

            if (movie.gotReviews()) {
                Log.d(LOG_LABEL, "Found " + movieInfo.getReviews().size() + " reviews for movie in info");
                for (MovieReview review : movieInfo.getReviews()) {
                    Log.d(LOG_LABEL, "Review: " + review.getAuthor());
                    if (review.getId().equals(reviewId)) {
                        Log.d(LOG_LABEL, "Found review for detail view");
                        this.review = review;
                        binding.setReview(review);
                        binding.executePendingBindings();
                        break;
                    }
                }

                if (review == null) {
                    Log.e(LOG_LABEL, "Could not find review for movie " + movieId + " with ID " + reviewId);
                }
            } else {
                Log.w(LOG_LABEL, "Have no reviews yet for movie");
                viewModel.loadMovieReviews(movieId).observe(this, response -> {
                    if (response == null || response.data == null) {
                        Log.w(LOG_LABEL, "no reviews found yet for movie...");
                        return;
                    }
                    // outside observer should refresh itself, so nothing to do here
                    Log.d(LOG_LABEL, "Loaded reviews for movie.");
                    viewModel.loadMovieReviews(movieId).removeObservers(this);
                });
            }

            ActionBar actionBar = getSupportActionBar();

            if (actionBar == null) {
                Log.e(LOG_LABEL, "could not find detail action bar");
                return;
            }

            actionBar.setTitle(movie.getTitle());
            actionBar.setDisplayHomeAsUpEnabled(true);
        });
    }
}
