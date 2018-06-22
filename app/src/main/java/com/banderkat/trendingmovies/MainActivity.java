package com.banderkat.trendingmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.banderkat.trendingmovies.data.MovieViewModel;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.Status;
import com.banderkat.trendingmovies.di.MovieViewModelFactory;
import com.banderkat.trendingmovies.trendingmovies.R;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private  static final String LOG_LABEL = "MainActivity";
    public static final int NUM_COLUMNS = 2;

    @SuppressWarnings("WeakerAccess")
    @Inject
    MovieViewModelFactory viewModelFactory;
    @SuppressWarnings("WeakerAccess")
    MovieViewModel viewModel;

    RecyclerView recyclerView;
    Menu menu;
    MoviePosterAdapter adapter;

    private boolean sortByMostPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_activity_gridview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUM_COLUMNS));

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
                } else {
                    Log.e(LOG_LABEL, "Response was null");
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

            // Reset list adapter if either it isn't set up, or if a filter was applied/removed.
            if (adapter == null || response.data.size() != adapter.getItemCount()) {
                adapter = new MoviePosterAdapter(this, recyclerView.getMeasuredWidth());
                // must set the list before the adapter for the differ to initialize properly
                adapter.submitList(response.data);
                recyclerView.setAdapter(adapter);
            } else {
                Log.d(LOG_LABEL, "submit list for diff");
                // Let the AsyncListDiffer find which have changed, and only update their view holders
                // https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter
                adapter.submitList(response.data);
            }
            adapter.notifyDataSetChanged();
            recyclerView.requestLayout();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.action_sort_most_popular:
                Log.d(LOG_LABEL, "sort most popular");
                sortByMostPopular = true;
                menu.getItem(1).setChecked(false);
                return true;
            case R.id.action_sort_top_rated:
                Log.d(LOG_LABEL, "sort top rated");
                sortByMostPopular = false;
                menu.getItem(0).setChecked(false);
                return true;
            default:
                Log.e(LOG_LABEL, "Unrecognized menu option " + item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        this.menu = menu;
        return true;
    }
}
