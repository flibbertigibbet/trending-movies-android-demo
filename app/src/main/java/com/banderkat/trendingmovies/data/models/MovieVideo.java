package com.banderkat.trendingmovies.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movie_video")
public class MovieVideo {

    @ColumnInfo(name = "movie_id")
    private long movieId;

    @PrimaryKey
    @NonNull
    private final String id;

    @SerializedName("iso_639_1")
    private final String language;

    @SerializedName("iso_3166_1")
    private final String country;

    private final String key;

    private final String name;

    private final String site;

    private final Integer size;

    private final String type;


    public MovieVideo(String id, String language, String country, String key, String name, String site, Integer size, String type) {
        this.id = id;
        this.language = language;
        this.country = country;
        this.key = key != null ? key : "";
        this.name = name != null ? name : "";
        this.site = site;
        this.size = size;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Video for movie #" + id + ": " + name;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public Integer getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}
