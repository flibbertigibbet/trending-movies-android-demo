package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.os.AsyncTask;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.networkresource.MoviePagedDataSource;

import java.util.List;

@Dao
public abstract class MovieDao {

    @Query("SELECT * from movie WHERE popular_page = :pageNumber ORDER BY popular_page_order ASC")
    public abstract MoviePagedDataSource.Factory<Integer, Movie> getPopularMovies(int pageNumber);

    @Query("SELECT * from movie WHERE top_rated_page = :pageNumber ORDER BY top_rated_page_order ASC")
    public abstract MoviePagedDataSource.Factory<Integer, Movie> getTopRatedMovies(long pageNumber);

    @Query("SELECT * from movie")
    public abstract LiveData<List<Movie>> getMovies();

    @Query("SELECT * FROM movie WHERE id = :movieId")
    public abstract LiveData<Movie> getMovie(long movieId);

    @SuppressWarnings("StaticFieldLeak")
    @Query("DELETE FROM movie")
    public void clear() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                clear();
                return null;
            }
        }.execute();
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(Movie obj);

    @Update()
    public abstract void update(Movie obj);

    @Delete()
    public abstract void delete(Movie obj);
}
