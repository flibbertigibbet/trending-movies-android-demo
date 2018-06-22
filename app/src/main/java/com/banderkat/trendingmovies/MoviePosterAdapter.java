package com.banderkat.trendingmovies;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.trendingmovies.BR;
import com.banderkat.trendingmovies.trendingmovies.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static com.banderkat.trendingmovies.MainActivity.NUM_COLUMNS;


public class  MoviePosterAdapter extends PagedListAdapter {

    private static final String LOG_LABEL = "PosterAdapter";

    private LayoutInflater inflater;

    private static final double ASPECT_RATIO = 1.5;

    private PagedList<Movie> movies;
    private int parentWidth;

    private static class PosterViewHolder extends RecyclerView.ViewHolder {

        private final ViewDataBinding binding;

        PosterViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Movie info) {
            binding.setVariable(BR.movie, info);
            binding.setVariable(BR.position, getAdapterPosition());
            binding.executePendingBindings();
        }
    }

    public MoviePosterAdapter() {
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
    }

    public MoviePosterAdapter(Context context, int parentWidth) {
        this();
        this.inflater = LayoutInflater.from(context);
        this.parentWidth = parentWidth;
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewDataBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.poster_grid_item, parent, false);
        binding.setVariable(BR.adapter, this);

        PosterViewHolder holder = new PosterViewHolder(binding);

        // set minimum height relative to column width
        int imageWidth = parentWidth / NUM_COLUMNS;
        holder.itemView.setMinimumHeight((int) (ASPECT_RATIO * imageWidth));
        holder.itemView.setMinimumWidth(imageWidth);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Movie movie = getItem(position);
        ((PosterViewHolder)holder).bind(movie);
        holder.itemView.setTag(movie);
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

    @Override
    public void submitList(PagedList pagedList) {
        super.submitList(pagedList);
        this.movies = pagedList;
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .fit()
                .into(view);
    }
}
