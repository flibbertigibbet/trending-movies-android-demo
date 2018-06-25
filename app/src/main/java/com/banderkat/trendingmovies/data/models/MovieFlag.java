package com.banderkat.trendingmovies.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "movie_flag")
public class MovieFlag {

    @PrimaryKey
    private final long id;

    @ColumnInfo(index = true)
    private boolean favorite;

    public MovieFlag(long id, boolean favorite) {
        this.id = id;
        this.favorite = favorite;
    }

    public long getId() {
        return id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
