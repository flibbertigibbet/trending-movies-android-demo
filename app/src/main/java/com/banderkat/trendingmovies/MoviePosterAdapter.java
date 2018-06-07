package com.banderkat.trendingmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.trendingmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class  MoviePosterAdapter extends BaseAdapter {

    private static final String LOG_LABEL = "PosterAdapter";

    private final Context context;
    private final LayoutInflater inflater;
    private final Picasso picasso;

    private static final int NUM_COLUMNS = 2;
    private static final double ASPECT_RATIO = 1.5;

    private List<Movie> movies;

    private static class ViewHolder {
        ImageView imageView;
    }

    public MoviePosterAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);

        // configure Picasso with logging
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.loggingEnabled(true);
        builder.listener((picasso, uri, exception) -> {
            Log.e(LOG_LABEL, "Failed to load image from " + uri.toString());
            exception.printStackTrace();
        });

        picasso = builder.build();
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.poster_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.poster_grid_item_image);
            convertView.setTag(viewHolder);
            // set minimum height relative to column width
            int imageWidth = parent.getWidth() / NUM_COLUMNS;
            viewHolder.imageView.setMinimumHeight((int)(ASPECT_RATIO * imageWidth));
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Movie movie = getItem(position);
        picasso.load(movie.getPosterPath()).fit().into(viewHolder.imageView);
        viewHolder.imageView.setContentDescription(movie.getTitle());

        return convertView;
    }
}
