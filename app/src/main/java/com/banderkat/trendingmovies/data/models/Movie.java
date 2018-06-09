package com.banderkat.trendingmovies.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(indices = {
        @Index(value={"popular_page", "popular_page_order"}),
        @Index(value={"trending_page", "trending_page_order"})
})
public class Movie {

    @Ignore
    private static final String POSTER_PATH_BASE = "http://image.tmdb.org/t/p/w185/";

    @PrimaryKey
    private final long id;

    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    private final long voteCount;

    private final boolean video;

    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    private final float voteAverage;

    private final String title;

    private final float popularity;

    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    private final String posterPath;

    @SerializedName("original_language")
    @ColumnInfo(name = "original_language")
    private final String originalLanguage;

    @SerializedName("original_title")
    @ColumnInfo(name = "original_title")
    private final String originalTitle;


    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    private final String releaseDate;

    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    private final String backdropPath;

    private final boolean adult;

    private final String overview;

    // timestamp is not final, as it is set on database save, and not by serializer
    private long timestamp;

    // track if movie was from the popular or trending endpoint, and where
    @ColumnInfo(name = "popular_page", index = true)
    private long popularPage;
    @ColumnInfo(name = "popular_page_order", index = true)
    private long popularPageOrder;
    @ColumnInfo(name = "trending_page", index = true)
    private long trendingPage;
    @ColumnInfo(name = "trending_page_order", index = true)
    private long trendingPageOrder;

    public Movie(long id, long voteCount, boolean video, float voteAverage, String title,
                 float popularity, String posterPath, String originalLanguage, String originalTitle,
                 String backdropPath, boolean adult, String overview, String releaseDate) {

        this.id = id;
        this.voteCount = voteCount;
        this.video = video;
        this.voteAverage = voteAverage;
        this.title = title;
        this.popularity = popularity;
        this.posterPath = posterPath != null && !posterPath.isEmpty() ? POSTER_PATH_BASE + posterPath : "";
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.backdropPath = backdropPath != null && !backdropPath.isEmpty() ? POSTER_PATH_BASE + backdropPath : "";
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;

        // flag -1 to indicate not seen yet
        popularPage = -1;
        popularPageOrder = -1;
        trendingPage = -1;
        trendingPageOrder = -1;
    }

    @Override
    public String toString() {
        return title != null && !title.isEmpty() ? title : "Untitled Movie";
    }

    // Setters

    /**
     * Timestamp entries. Timestamp value does not come from query result; it should be set
     * on database save. Gson serializer will initialize value to zero.
     *
     * @param timestamp Time in milliseconds since Unix epoch
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPopularPage(long popularPage) {
        this.popularPage = popularPage;
    }

    public void setPopularPageOrder(long popularPageOrder) {
        this.popularPageOrder = popularPageOrder;
    }

    public void setTrendingPage(long trendingPage) {
        this.trendingPage = trendingPage;
    }

    public void setTrendingPageOrder(long trendingPageOrder) {
        this.trendingPageOrder = trendingPageOrder;
    }

    // Getters

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

    public long getPopularPage() {
        return popularPage;
    }

    public long getPopularPageOrder() {
        return popularPageOrder;
    }

    public long getTrendingPage() {
        return trendingPage;
    }

    public long getTrendingPageOrder() {
        return trendingPageOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id &&
                voteCount == movie.voteCount && video == movie.video &&
                Float.compare(movie.voteAverage, voteAverage) == 0 &&
                Float.compare(movie.popularity, popularity) == 0 &&
                adult == movie.adult &&
                timestamp == movie.timestamp &&
                popularPage == movie.popularPage &&
                popularPageOrder == movie.popularPageOrder &&
                trendingPage == movie.trendingPage &&
                trendingPageOrder == movie.trendingPageOrder &&
                Objects.equals(title, movie.title) &&
                Objects.equals(posterPath, movie.posterPath) &&
                Objects.equals(originalLanguage, movie.originalLanguage) &&
                Objects.equals(originalTitle, movie.originalTitle) &&
                Objects.equals(releaseDate, movie.releaseDate) &&
                Objects.equals(backdropPath, movie.backdropPath) &&
                Objects.equals(overview, movie.overview);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, voteCount, video, voteAverage, title, popularity, posterPath, originalLanguage, originalTitle, releaseDate, backdropPath, adult, overview, timestamp, popularPage, popularPageOrder, trendingPage, trendingPageOrder);
    }
}
