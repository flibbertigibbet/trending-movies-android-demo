package com.banderkat.trendingmovies.di;

import com.banderkat.trendingmovies.ReviewActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ReviewActivityModule {
    @ContributesAndroidInjector
    abstract ReviewActivity contributeReviewActivity();
}
