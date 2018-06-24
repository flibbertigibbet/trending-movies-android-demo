package com.banderkat.trendingmovies.data.networkresource;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;


/**
 * Based on:
 * https://developer.android.com/topic/libraries/architecture/guide.html
 *
 * Modified to support pagination.
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private static final String LOG_LABEL = "NetworkBound";

    public static final int FIRST_PAGE = 1;

    protected final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    protected abstract boolean fetchPageFromNetwork(@Nullable ResultType data, int pageNumber);

    // Called to get the cached data from the database
    @NonNull @MainThread
    protected abstract LiveData<ResultType> loadFromDb(int pageNumber);

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall(int pageNumber);

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @SuppressWarnings("WeakerAccess")
    @MainThread
    protected void onFetchFailed() {
        Log.w(LOG_LABEL, "fetch request failed");
    }

    @MainThread
    protected void setupSource(int pageNum) {
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb(pageNum);
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (fetchPageFromNetwork(data, pageNum)) {
                fetchFromNetwork(dbSource, pageNum);
            } else {
                //noinspection ConstantConditions
                result.addSource(dbSource,
                        newData -> {
                    result.setValue(Resource.success(newData));
                    result.removeSource(dbSource);
                });
            }
        });
    }

    protected void fetchFromNetwork(final LiveData<ResultType> dbSource, int pageNumber) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall(pageNumber);
        // we re-attach dbSource as a new source,
        // it will dispatch its latest value quickly
        result.addSource(dbSource,
                newData -> result.setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                saveResultAndReInit(response, pageNumber);
            } else {
                onFetchFailed();
                result.addSource(dbSource,
                        newData -> result.setValue(
                                Resource.error(response.errorMessage, newData)));
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    private void saveResultAndReInit(ApiResponse<RequestType> response, int pageNumber) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (response.body != null) {
                    saveCallResult(response.body);
                } else {
                    Log.w(LOG_LABEL, "Received null API response");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest results received from network.
                //noinspection ConstantConditions
                result.addSource(loadFromDb(pageNumber),
                        newData -> result.setValue(Resource.success(newData)));
            }
        }.execute();
    }

    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }
}

