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
import android.support.annotation.TransitionRes;

import com.banderkat.trendingmovies.data.models.MovieVideo;

import java.util.List;

@Dao
public abstract class VideoDao {

    @Query("SELECT * from movie_video WHERE id =:movieId")
    public abstract LiveData<List<MovieVideo>> getMovieVideos(long movieId);

    @SuppressWarnings("StaticFieldLeak")
    @Query("DELETE FROM movie_video")
    public void clear() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                clear();
                return null;
            }
        }.execute();
    }

    @Query("UPDATE movie SET got_videos = 1 WHERE id = :movieId")
    public abstract void gotVideos(long movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(MovieVideo obj);

    @Transaction
    public void saveVideos(long movieId, List<MovieVideo> videos) {
        for (MovieVideo video: videos) {
            video.setMovieId(movieId);
            save(video);
        }
        gotVideos(movieId);
    }

    @Update()
    public abstract void update(MovieVideo obj);

    @Delete()
    public abstract void delete(MovieVideo obj);
}
