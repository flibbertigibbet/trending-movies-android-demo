package com.banderkat.trendingmovies.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.banderkat.trendingmovies.data.models.Movie;
import com.banderkat.trendingmovies.data.models.MovieReview;
import com.banderkat.trendingmovies.data.models.MovieVideo;

@Database(version=5, entities={Movie.class, MovieVideo.class, MovieReview.class})
@TypeConverters({RoomConverters.class})
public abstract class MovieDatabase extends RoomDatabase {
    abstract public MovieDao movieDao();
    abstract public VideoDao videoDao();
    abstract public ReviewDao reviewDao();
}
