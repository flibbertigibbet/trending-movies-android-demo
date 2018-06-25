package com.banderkat.trendingmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SimpleExpandableListAdapter;

import com.banderkat.trendingmovies.data.MovieViewModel;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieFlag;
import com.banderkat.trendingmovies.data.models.MovieInfo;
import com.banderkat.trendingmovies.data.models.MovieReview;
import com.banderkat.trendingmovies.data.models.MovieVideo;
import com.banderkat.trendingmovies.di.MovieViewModelFactory;
import com.banderkat.trendingmovies.trendingmovies.R;
import com.banderkat.trendingmovies.trendingmovies.databinding.ActivityMovieDetailBinding;
import com.banderkat.trendingmovies.trendingmovies.databinding.MovieDetailHeaderBinding;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.banderkat.trendingmovies.ReviewActivity.MOVIE_ID_REVIEW_KEY;
import static java.util.Locale.US;

public class MovieDetailActivity extends AppCompatActivity {
    @SuppressWarnings("WeakerAccess")
    @Inject
    MovieViewModelFactory viewModelFactory;
    @SuppressWarnings("WeakerAccess")
    MovieViewModel viewModel;

    public static final String MOVIE_ID_DETAIL_KEY = "movie_id";
    private static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private static final String LOG_LABEL = "MovieDetail";
    private static final DateFormat releaseDateFormat, displayFormat;

    static {
        releaseDateFormat = new SimpleDateFormat("y-M-d", US);
        displayFormat = new SimpleDateFormat("MMMM d, y", US);
    }

    private long movieId;
    private Movie movie;
    private MovieInfo movieInfo;
    private ActivityMovieDetailBinding binding;
    private MovieDetailHeaderBinding headerBinding;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        View header = getLayoutInflater().inflate(R.layout.movie_detail_header, null);
        headerBinding = DataBindingUtil.bind(header);
        binding.movieDetailExpandableList.addHeaderView(header);

        LiveData<MovieInfo> data = viewModel.getMovieInfo(movieId);
        data.observe(this, movieInfo -> {
            if (movieInfo == null || movieInfo.getMovie() == null) {
                Log.e(LOG_LABEL, "Could not find movie to show detail for ID: " + movieId);
                return;
            }

            this.movieInfo = movieInfo;
            this.movie = movieInfo.getMovie();
            Log.d(LOG_LABEL, "Loaded data for movie " + movie.getTitle());

            if (movie.gotVideos()) {
                Log.d(LOG_LABEL, "Found " + movieInfo.getVideos().size() + " videos for movie in info");
                for (MovieVideo video : movieInfo.getVideos()) {
                    Log.d(LOG_LABEL, "Video: " + video.getName() + ": " + video.getKey());
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

            if (movie.gotReviews()) {
                Log.d(LOG_LABEL, "Found " + movieInfo.getReviews().size() + " reviews for movie in info");
                for (MovieReview review : movieInfo.getReviews()) {
                    Log.d(LOG_LABEL, "Review: " + review.getAuthor());
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

            if (movie.gotVideos() && movie.gotReviews()) {
                data.removeObservers(this);
                setUpExpandableList(movieInfo);
            }

            headerBinding.setMovie(movie);
            headerBinding.setMovieInfo(movieInfo);
            headerBinding.setActivity(this);

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
                    .into(headerBinding.movieDetailPoster);

            headerBinding.movieDetailFavoriteButton.setOnClickListener(v -> {
                boolean newFavorite = !movieInfo.isFavorite();
                MovieFlag flag = new MovieFlag(movieId, newFavorite);
                movieInfo.setFavorite(newFavorite);

                Log.d(LOG_LABEL, "setting movie favorite flag to: " + newFavorite);
                viewModel.setMovieFlag(flag);
                Log.d(LOG_LABEL, "favorite? " + movieInfo.isFavorite());
                headerBinding.setMovieInfo(movieInfo);
            });
        });
    }

    private void setUpExpandableList(MovieInfo movieInfo) {
        final String groupName = "GROUP_NAME";
        final String valueName = "CHILD_VALUE";
        final String nameValue = "CHILD_NAME";

        final String videoGroup = getString(R.string.details_videos_group);
        final String reviewsGroup = getString(R.string.details_reviews_group);

        List<HashMap<String, String>> groups = new ArrayList<>(2);
        HashMap<String, String> videos = new HashMap<>(1);
        videos.put(groupName, videoGroup);
        groups.add(videos);
        HashMap<String, String> reviews = new HashMap<>(1);
        reviews.put(groupName, reviewsGroup);
        groups.add(reviews);

        List<List<Map<String, String>>> childData = new ArrayList<>(2);
        ArrayList<Map<String, String>> videoList = new ArrayList<>(movieInfo.getVideos().size());

        for (MovieVideo video : movieInfo.getVideos()) {
            HashMap<String, String> videoMap = new HashMap<>(2);
            videoMap.put(nameValue, video.getName());
            videoMap.put(valueName, video.getType());
            videoList.add(videoMap);
        }

        childData.add(videoList);

        ArrayList<Map<String, String>> reviewsList = new ArrayList<>(movieInfo.getReviews().size());
        for (MovieReview review : movieInfo.getReviews()) {
            HashMap<String, String> reviewMap = new HashMap<>(2);
            reviewMap.put(nameValue, review.getAuthor());
            reviewMap.put(valueName, review.getContent());
            reviewsList.add(reviewMap);
        }

        childData.add(reviewsList);

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this,
                groups,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {groupName},
                new int[] { android.R.id.text1 },
                childData,
                R.layout.ellipsized_expandable_list_item_2,
                new String[] { nameValue, valueName },
                new int[] { android.R.id.text1, android.R.id.text2 });

        binding.movieDetailExpandableList.setAdapter(adapter);

        binding.movieDetailExpandableList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Log.d(LOG_LABEL, "Clicked on child " + childPosition + " in group " + groupPosition);

            switch (groupPosition) {
                case 0:
                    // Video row clicked
                    goToVideoAtIndex(childPosition);
                    break;
                case 1:
                    // Review row clicked
                    goToReviewAtIndex(childPosition);
                    break;
                default:
                    Log.e(LOG_LABEL, "unrecognized group " + groupPosition);
            }
            return true;
        });

        Log.d(LOG_LABEL, "Set up expandable list adapter");
    }

    private void goToReviewAtIndex(int index) {
        if (movie == null || !movie.gotReviews() || movieInfo == null || movieInfo.getReviews() == null) {
            Log.e(LOG_LABEL, "Cannot get review for index " + index);
            return;
        }

        Log.d(LOG_LABEL, "go to review for index " + index);
        MovieReview review = movieInfo.getReviews().get(index);
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra(MOVIE_ID_DETAIL_KEY, movieId);
        intent.putExtra(MOVIE_ID_REVIEW_KEY, review.getId());
        startActivity(intent);
    }

    private void goToVideoAtIndex(int index) {
        if (movie == null || !movie.gotVideos() || movieInfo == null || movieInfo.getVideos() == null) {
            Log.e(LOG_LABEL, "Cannot get video for index " + index);
            return;
        }

        Log.d(LOG_LABEL, "go to video for index " + index);
        MovieVideo video = movieInfo.getVideos().get(index);

        // Launch YouTube player, or fallback on opening in web browser
        // based on https://stackoverflow.com/a/12439378
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_URL + video.getKey()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
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
