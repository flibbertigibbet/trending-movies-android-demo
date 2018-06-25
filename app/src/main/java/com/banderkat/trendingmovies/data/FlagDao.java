package com.banderkat.trendingmovies.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.os.AsyncTask;

import com.banderkat.trendingmovies.data.models.MovieFlag;


@Dao
public abstract class FlagDao {
    @SuppressWarnings("StaticFieldLeak")
    @Query("DELETE FROM movie")
    public void clear() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                clear();
                return null;
            }
        }.execute();
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(MovieFlag obj);

    @Update()
    public abstract void update(MovieFlag obj);

    @Delete()
    public abstract void delete(MovieFlag obj);
}
