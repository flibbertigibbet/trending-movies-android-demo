package com.banderkat.trendingmovies.trendingmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class  MoviePosterAdapter extends BaseAdapter {

    public static final String EXAMPLE_POSTER_URL = "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";

    private static final String LOG_LABEL = "PosterAdapter";

    private final Context context;
    private final LayoutInflater inflater;
    private final Picasso picasso;

    private static final int NUM_COLUMNS = 2;
    private static final double ASPECT_RATIO = 1.5;

    private static class ViewHolder {
        ImageView imageView;
    }

    public MoviePosterAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        // configure Picasso with logging
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.loggingEnabled(true);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.e(LOG_LABEL, "Failed to load image from " + uri.toString());
                exception.printStackTrace();
            }
        });

        picasso = builder.build();
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        picasso.load(EXAMPLE_POSTER_URL).fit().into(viewHolder.imageView);

        // FIXME: load data and set description for image
        viewHolder.imageView.setContentDescription("FIXME");

        return convertView;
    }
}
