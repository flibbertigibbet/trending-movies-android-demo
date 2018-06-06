package com.banderkat.trendingmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.banderkat.trendingmovies.data.models.Movie;

import java.util.List;

@Dao
public abstract class MovieDao {

    @Query("SELECT * FROM movie WHERE id = :movieId")
    abstract LiveData<Movie> getMovie(long movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void save(Movie obj);

    @Update()
    abstract void update(Movie obj);

    @Delete()
    abstract void delete(Movie obj);
}
