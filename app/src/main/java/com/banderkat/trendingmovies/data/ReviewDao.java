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
import android.util.Log;

import com.banderkat.trendingmovies.data.models.MovieReview;

import java.util.List;

@Dao
public abstract class ReviewDao {

    private static final String LOG_LABEL = "ReviewDao";

    @Query("SELECT * from movie_review WHERE id =:movieId")
    public abstract LiveData<List<MovieReview>> getMovieReviews(long movieId);

    @SuppressWarnings("StaticFieldLeak")
    @Query("DELETE FROM movie_review")
    public void clear() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                clear();
                return null;
            }
        }.execute();
    }

    @Query("UPDATE movie SET got_reviews = 1 WHERE id = :movieId")
    public abstract void gotReviews(long movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(MovieReview obj);

    @Transaction
    public void saveReviews(long movieId, List<MovieReview> reviews) {
        for (MovieReview review : reviews) {
            Log.d(LOG_LABEL, "Saving review by " + review.getAuthor());
            review.setMovieId(movieId);
            save(review);
        }
        gotReviews(movieId);
    }

    @Update()
    public abstract void update(MovieReview obj);

    @Delete()
    public abstract void delete(MovieReview obj);
}
