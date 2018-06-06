package com.banderkat.trendingmovies.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Movie {

    @PrimaryKey
    private final int id;

    public Movie(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
