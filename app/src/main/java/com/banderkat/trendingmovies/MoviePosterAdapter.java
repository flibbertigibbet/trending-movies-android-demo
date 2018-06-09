package com.banderkat.trendingmovies;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.trendingmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;


public class  MoviePosterAdapter extends PagedListAdapter {

    private static final String LOG_LABEL = "PosterAdapter";

    private final Context context;
    private final LayoutInflater inflater;
    private final Picasso picasso;

    private static final int NUM_COLUMNS = 2;
    private static final double ASPECT_RATIO = 1.5;

    private List<Movie> movies;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }
    }

    public MoviePosterAdapter(Context context, List<Movie> movies) {
        super(new DiffUtil.ItemCallback<Movie>() {
            @Override
            public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
                if (oldItem == null) {
                    return newItem == null;
                } else if (newItem == null) {
                    return  false;
                }

                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
        this.context = context;
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);

        // configure Picasso with logging
        Picasso.Builder builder = new Picasso.Builder(context);
        // builder.loggingEnabled(true);
        builder.listener((picasso, uri, exception) -> {
            Log.e(LOG_LABEL, "Failed to load image from " + uri.toString());
            exception.printStackTrace();
        });

        picasso = builder.build();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.poster_grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder((ImageView)convertView);
        viewHolder.imageView = convertView.findViewById(R.id.poster_grid_item_image);
        convertView.setTag(viewHolder);
        // set minimum height relative to column width
        int imageWidth = parent.getWidth() / NUM_COLUMNS;
        viewHolder.imageView.setMinimumHeight((int)(ASPECT_RATIO * imageWidth));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Movie movie = getItem(position);
        ImageView imageView = ((ViewHolder)viewHolder).imageView;
        picasso.load(movie.getPosterPath()).fit().into(imageView);
        imageView.setContentDescription(movie.getTitle());
    }

    @Override
    public long getItemId(int position) {
        Movie movie = getItem(position);
        if (movie != null) {
            return  movie.getId();
        } else {
            return -1;
        }
    }
}
