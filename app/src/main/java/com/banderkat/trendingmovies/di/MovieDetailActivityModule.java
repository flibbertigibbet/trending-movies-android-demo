package com.banderkat.trendingmovies.di;

import com.banderkat.trendingmovies.MovieDetailActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MovieDetailActivityModule {
    @ContributesAndroidInjector
    abstract MovieDetailActivity contributeMovieDetailActivity();
}
