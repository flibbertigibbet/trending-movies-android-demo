package com.banderkat.trendingmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.banderkat.trendingmovies.data.MovieViewModel;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.Status;
import com.banderkat.trendingmovies.di.MovieViewModelFactory;
import com.banderkat.trendingmovies.trendingmovies.R;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private  static final String LOG_LABEL = "MainActivity";

    @SuppressWarnings("WeakerAccess")
    @Inject
    MovieViewModelFactory viewModelFactory;
    @SuppressWarnings("WeakerAccess")
    MovieViewModel viewModel;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_activity_gridview);

        Log.d(LOG_LABEL, "Going to query for data");

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieViewModel.class);
        viewModel.loadMovies(false).observe(this, response -> {
            if (response == null || response.data == null) {
                if (response != null) {
                    if (response.status == Status.LOADING) {
                        Log.d(LOG_LABEL, "Waiting for data to load...");
                        return;
                    }
                    Log.e(LOG_LABEL, response.status + ": " + response.status.name());
                }
                Log.e(LOG_LABEL, "Failed to query for movies");
                return;
            }

            if (response.data.isEmpty()) {
                Log.w(LOG_LABEL, "Found no movies.");
                return;
            }

            Log.d(LOG_LABEL, "Found " + response.data.size() + " movies!");

            for (Movie movie: response.data) {
                Log.d(LOG_LABEL, "Movie: " + movie.toString());
            }


            MoviePosterAdapter adapter = new MoviePosterAdapter(this, response.data);
            recyclerView.setAdapter(adapter);

        });
    }
}
