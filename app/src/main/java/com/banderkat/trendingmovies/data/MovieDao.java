package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.os.AsyncTask;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieInfo;
import com.banderkat.trendingmovies.data.networkresource.MoviePopularPagedDataSource;
import com.banderkat.trendingmovies.data.networkresource.MovieTopRatedPagedDataSource;

import java.util.List;

@Dao
public abstract class MovieDao {

    @Query("SELECT * FROM movie WHERE popular_page = :pageNumber ORDER BY popular_page_order ASC")
    public abstract MoviePopularPagedDataSource.Factory<Integer, Movie> getPopularMovies(int pageNumber);

    @Query("SELECT * FROM movie WHERE top_rated_page = :pageNumber ORDER BY top_rated_page_order ASC")
    public abstract MovieTopRatedPagedDataSource.Factory<Integer, Movie> getTopRatedMovies(long pageNumber);

    @Query("SELECT movie.* FROM movie " +
            "INNER JOIN movie_flag ON movie.id = movie_flag.id " +
            "WHERE movie_flag.favorite = 1 " +
            "ORDER BY movie.title ASC")
    public abstract MovieTopRatedPagedDataSource.Factory<Integer, Movie> getFavoriteMovies();

    @Query("SELECT * FROM movie")
    public abstract LiveData<List<Movie>> getMovies();

    @Query("SELECT * FROM movie WHERE id = :movieId")
    public abstract LiveData<Movie> getMovie(long movieId);

    @Transaction
    @Query("SELECT movie.*, movie_flag.favorite AS favorite " +
            "FROM movie " +
            "LEFT JOIN movie_flag ON movie.id = movie_flag.id " +
            "WHERE movie.id = :movieId")
    public abstract LiveData<MovieInfo> getMovieInfo(long movieId);

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
