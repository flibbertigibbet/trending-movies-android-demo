package com.banderkat.trendingmovies.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.banderkat.trendingmovies.data.MovieViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Support dependency injection of ViewModels.
 *
 * Based on:
 * https://github.com/googlesamples/android-architecture-components/blob/e33782ba54ebe87f7e21e03542230695bc893818/GithubBrowserSample/app/src/main/java/com/android/example/github/di/ViewModelModule.java
 */

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MovieViewModel.class)
    abstract ViewModel bindMovieViewModel(MovieViewModel movieViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MovieViewModelFactory factory);
}
