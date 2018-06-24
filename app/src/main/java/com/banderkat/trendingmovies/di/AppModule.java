package com.banderkat.trendingmovies.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.util.Log;

import com.banderkat.trendingmovies.data.MovieDao;
import com.banderkat.trendingmovies.data.MovieDatabase;
import com.banderkat.trendingmovies.data.MovieWebservice;
import com.banderkat.trendingmovies.data.VideoDao;
import com.banderkat.trendingmovies.data.networkresource.LiveDataCallAdapterFactory;
import com.banderkat.trendingmovies.trendingmovies.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjectionModule;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Inject singleton dependencies for use across the app.
 *
 * Based on:
 * https://github.com/googlesamples/android-architecture-components/blob/e33782ba54ebe87f7e21e03542230695bc893818/GithubBrowserSample/app/src/main/java/com/android/example/github/di/AppModule.java
 */
@Module(includes = {ViewModelModule.class, AndroidInjectionModule.class})
class AppModule {

    private static final String DATABASE_NAME = "banderkat-trending-movies";

    @Singleton
    @Provides
    MovieWebservice provideMovieWebservice() {
        // add network query logging by setting client on Retrofit
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);
        OkHttpClient client = builder.build();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl(MovieWebservice.WEBSERVICE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(MovieWebservice.class);
    }

    @Singleton
    @Provides
    MovieDatabase provideDatabase(Application app) {
        return Room.databaseBuilder(app, MovieDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    MovieDao provideMovieDao(MovieDatabase db) {
        Log.d("AppModule", "provideMovieDao");
        return db.movieDao();
    }

    @Singleton
    @Provides
    VideoDao provideVideoDao(MovieDatabase db) {
        return db.videoDao();
    }

    @Singleton
    @Provides
    String provideApiKey(Application app) {
        return app.getApplicationContext().getString(R.string.api_key);
    }
}
