package com.banderkat.trendingmovies;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.banderkat.trendingmovies.MainActivity.NUM_COLUMNS;


public class  MoviePosterAdapter extends ListAdapter<Movie, MoviePosterAdapter.PosterViewHolder> {

    public static final int SCROLL_READ_AHEAD = 2;

    public interface MoviePosterClickListener {
        void onItemClick(long movieId);
        void loadNext(int index);
    }

    public static final String POSTER_PICASSO_GROUP = "movie_posters";

    private final MoviePosterClickListener listener;

    private static final String LOG_LABEL = "PosterAdapter";
    private LayoutInflater inflater;

    public static final double ASPECT_RATIO = 1.5;

    private ArrayList<Movie> movies;
    private int parentWidth;

    public static class PosterViewHolder extends RecyclerView.ViewHolder {

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

    private MoviePosterAdapter(MoviePosterClickListener listener) {
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

        setHasStableIds(true);
        this.listener = listener;
    }

    public MoviePosterAdapter(Context context, int parentWidth, MoviePosterClickListener listener) {
        this(listener);
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
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        Movie movie = getItem(position);
        holder.bind(movie);
        holder.itemView.setTag(movie);

        int itemCount = getItemCount();
        if (itemCount >= SCROLL_READ_AHEAD && position > itemCount - SCROLL_READ_AHEAD) {
            listener.loadNext(getItemCount() - 1);
        }

        holder.itemView.setOnClickListener(v -> {
            if (movie != null) {
                Log.d(LOG_LABEL, "selected movie " + movie.getTitle());
                listener.onItemClick(movie.getId());
            } else {
                Log.e(LOG_LABEL, "have no movie in view holder bind");
            }
        });
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

    public void appendList(List<Movie> list) {
        if (movies == null) {
            Log.w(LOG_LABEL, "Attempting to append to an uninitialized list");
            movies = new ArrayList<>(list.size());
        }
        movies.addAll(list);
    }

    @Override
    public void submitList(List<Movie> list) {
        if (movies == null) {
            movies = new ArrayList<>(list.size());
        }
        movies.clear();
        movies.addAll(list);
        super.submitList(movies);
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .tag(POSTER_PICASSO_GROUP)
                .fit()
                .into(view);
    }
}
