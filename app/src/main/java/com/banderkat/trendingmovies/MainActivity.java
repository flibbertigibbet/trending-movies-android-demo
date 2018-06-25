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
import static com.banderkat.trendingmovies.data.MovieRepository.PAGE_SIZE;
import static com.banderkat.trendingmovies.data.networkresource.NetworkBoundResource.FIRST_PAGE;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.MoviePosterClickListener {

    private  static final String LOG_LABEL = "MainActivity";
    public static final int NUM_COLUMNS = 2;

    private static final String SORT_BY_MOST_POPULAR_KEY = "sort_popular";
    private static final String FILTER_TO_FAVORITES_KEY = "filter_favorites";

    @SuppressWarnings("WeakerAccess")
    @Inject
    MovieViewModelFactory viewModelFactory;
    @SuppressWarnings("WeakerAccess")
    MovieViewModel viewModel;

    RecyclerView recyclerView;
    Menu menu;
    MoviePosterAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private boolean sortByMostPopular = true;
    private boolean sortByFavorited = false;

    private LiveData<Resource<PagedList<Movie>>> liveData;
    private PagedList<Movie> pagedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_activity_gridview);
        layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        recyclerView.setLayoutManager(layoutManager);

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

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data in case filtered to favorites and user came back from detail view
        // where favorite may have changed.
        loadMovies();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SORT_BY_MOST_POPULAR_KEY, sortByMostPopular);
        outState.putBoolean(FILTER_TO_FAVORITES_KEY, sortByFavorited);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_BY_MOST_POPULAR_KEY) &&
                    savedInstanceState.containsKey(FILTER_TO_FAVORITES_KEY)) {
                Log.d(LOG_LABEL, "restoring settings from bundle");
                sortByMostPopular = savedInstanceState.getBoolean(SORT_BY_MOST_POPULAR_KEY, true);
                sortByFavorited = savedInstanceState.getBoolean(FILTER_TO_FAVORITES_KEY, false);
            }
        }

        Log.d(LOG_LABEL, "onRestoreInstanceState");
    }

    private void loadMovies() {
        Log.d(LOG_LABEL, "loadMovies. most popular? " + sortByMostPopular + " favorited? " + sortByFavorited);
        if (liveData != null) {
            liveData.removeObservers(this);
        }

        liveData = viewModel.loadMovies(sortByMostPopular, sortByFavorited);
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
            }

            pagedList = response.data;

            long page = FIRST_PAGE;
            if (!pagedList.isEmpty() && !sortByFavorited) {
                Movie movie = pagedList.get(0);
                if (movie != null) {
                    page = sortByMostPopular ? movie.getPopularPage() : movie.getTopRatedPage();
                }
            }

            Log.d(LOG_LABEL, "Got " + pagedList.size() + " movies for page #" + page);

            if (adapter == null) {
                adapter = new MoviePosterAdapter(this, recyclerView.getMeasuredWidth(), this);
                // must set the list before the adapter for the differ to initialize properly
                adapter.submitList(pagedList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                if (page <= FIRST_PAGE) {
                    // swap out adapter on first page load
                    MoviePosterAdapter newAdapter = adapter = new MoviePosterAdapter(this, recyclerView.getMeasuredWidth(), this);
                    newAdapter.submitList(pagedList);
                    recyclerView.swapAdapter(newAdapter, true);
                    adapter = newAdapter;
                    adapter.notifyDataSetChanged();
                } else {
                    // append next page of data to existing adapter
                    adapter.appendList(pagedList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.action_sort_most_popular:
                Log.d(LOG_LABEL, "sort most popular");
                sortByMostPopular = true;
                sortByFavorited = false;
                menu.getItem(1).setChecked(false);
                menu.getItem(2).setChecked(false);
                break;
            case R.id.action_sort_top_rated:
                Log.d(LOG_LABEL, "sort top rated");
                sortByMostPopular = false;
                sortByFavorited = false;
                menu.getItem(0).setChecked(false);
                menu.getItem(2).setChecked(false);
                break;
            case R.id.action_sort_favorites:
                Log.d(LOG_LABEL, "filter to favorited");
                sortByFavorited = true;
                sortByMostPopular = false;
                menu.getItem(0).setChecked(false);
                menu.getItem(1).setChecked(false);
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
        Log.d(LOG_LABEL, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        this.menu = menu;

        // restore menu settings
        if (sortByFavorited) {
            onOptionsItemSelected(menu.findItem(R.id.action_sort_favorites));
        } else if (!sortByMostPopular) {
            onOptionsItemSelected(menu.findItem(R.id.action_sort_top_rated));
        }

        return true;
    }

    @Override
    public void onItemClick(long movieId) {
        Log.d(LOG_LABEL, "in callback for selected movie ID " + movieId);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MOVIE_ID_DETAIL_KEY, movieId);
        startActivity(intent);
    }

    @Override
    public void loadNext(int index) {
        if (pagedList != null && !sortByFavorited) {
            Log.d(LOG_LABEL, "Scrolled to bottom; load next page");
            // support infinite scrolling with paginated results
            pagedList.loadAround(index + PAGE_SIZE);
        } else {
            Log.w(LOG_LABEL, "Have no paged list from which to load next page");
        }
    }
}
