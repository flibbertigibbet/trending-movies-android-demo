package com.banderkat.trendingmovies.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Movie {

    @PrimaryKey
    private final long id;

    @SerializedName("vote_count")
    private final long voteCount;

    private final boolean video;

    @SerializedName("vote_average")
    private final float voteAverage;

    private final String title;

    private final float popularity;

    @SerializedName("poster_path")
    private final String posterPath;

    @SerializedName("original_language")
    private final String originalLanguage;

    @SerializedName("original_title")
    private final String originalTitle;


    @SerializedName("release_date")
    private final String releaseDate;

    @SerializedName("backdrop_path")
    private final String backdropPath;

    private final boolean adult;

    private final String overview;

    // timestamp is not final, as it is set on database save, and not by serializer
    private long timestamp;

    public Movie(long id, long voteCount, boolean video, float voteAverage, String title,
                 float popularity, String posterPath, String originalLanguage, String originalTitle,
                 String backdropPath, boolean adult, String overview, String releaseDate) {

        this.id = id;
        this.voteCount = voteCount;
        this.video = video;
        this.voteAverage = voteAverage;
        this.title = title;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.backdropPath = backdropPath;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return title != null && !title.isEmpty() ? title : "Untitled Movie";
    }

    /**
     * Timestamp entries. Timestamp value does not come from query result; it should be set
     * on database save. Gson serializer will initialize value to zero.
     *
     * @param timestamp Time in milliseconds since Unix epoch
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getId() {
        return id;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
