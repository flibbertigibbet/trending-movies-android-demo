package com.banderkat.trendingmovies.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.banderkat.trendingmovies.data.models.Movie;

@Database(version=3, entities={Movie.class})
@TypeConverters({RoomConverters.class})
public abstract class MovieDatabase extends RoomDatabase {
    abstract public MovieDao movieDao();
}
