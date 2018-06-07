package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.banderkat.trendingmovies.data.models.Movie;

import java.util.List;

@Dao
public abstract class MovieDao {

    @Query("SELECT * from movie")
    public abstract LiveData<List<Movie>> getPopularMovies();

    @Query("SELECT * FROM movie WHERE id = :movieId")
    public abstract LiveData<Movie> getMovie(long movieId);

    @Query("DELETE FROM movie")
    public abstract void clear();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(Movie obj);

    @Update()
    public abstract void update(Movie obj);

    @Delete()
    public abstract void delete(Movie obj);
}
