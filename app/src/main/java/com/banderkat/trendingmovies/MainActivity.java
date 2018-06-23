package com.banderkat.trendingmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.banderkat.trendingmovies.data.MovieViewModel;
import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.Resource;
import com.banderkat.trendingmovies.data.networkresource.Status;
import com.banderkat.trendingmovies.di.MovieViewModelFactory;
import com.banderkat.trendingmovies.trendingmovies.R;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import static com.banderkat.trendingmovies.MovieDetailActivity.MOVIE_ID_DETAIL_KEY;
import static com.banderkat.trendingmovies.MoviePosterAdapter.POSTER_PICASSO_GROUP;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.MoviePosterClickListener {

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

    private LiveData<Resource<PagedList<Movie>>> liveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_activity_gridview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUM_COLUMNS));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                final Picasso picasso = Picasso.with(MainActivity.this);
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE ||
                        scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    picasso.resumeTag(POSTER_PICASSO_GROUP);
                } else {
                    picasso.pauseTag(POSTER_PICASSO_GROUP);
                }
            }
        });

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieViewModel.class);

        loadMovies();
    }

    private void loadMovies() {
        Log.d(LOG_LABEL, "loadMovies. most popular? " + sortByMostPopular);
        if (liveData != null) {
            liveData.removeObservers(this);
        }
        liveData = viewModel.loadMovies(sortByMostPopular);
        liveData.observe(this, response -> {
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

            Log.d(LOG_LABEL, "Found " + response.data.size() + " movies");

            for (Movie movie: response.data) {
                Log.d(LOG_LABEL, "Movie: " + movie.toString());
            }

            // Reset list adapter if either it isn't set up, or if a filter was applied/removed.
            if (adapter == null || response.data.size() != adapter.getItemCount()) {
                adapter = new MoviePosterAdapter(this, recyclerView.getMeasuredWidth(), this);
                // must set the list before the adapter for the differ to initialize properly
                adapter.submitList(response.data);
                recyclerView.setAdapter(adapter);
            } else {
                Log.d(LOG_LABEL, "swap adapters");
                MoviePosterAdapter newAdapter = adapter = new MoviePosterAdapter(this, recyclerView.getMeasuredWidth(), this);;
                newAdapter.submitList(response.data);
                recyclerView.swapAdapter(newAdapter, true);
                adapter = newAdapter;
            }
            adapter.notifyDataSetChanged();
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
                break;
            case R.id.action_sort_top_rated:
                Log.d(LOG_LABEL, "sort top rated");
                sortByMostPopular = false;
                menu.getItem(0).setChecked(false);
                break;
            default:
                Log.e(LOG_LABEL, "Unrecognized menu option " + item.getItemId());
                return super.onOptionsItemSelected(item);
        }

        loadMovies();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public void onItemClick(long movieId) {
        Log.d(LOG_LABEL, "in callback for selected movie ID " + movieId);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MOVIE_ID_DETAIL_KEY, movieId);
        startActivity(intent);
    }
}
