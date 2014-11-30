package com.android.potlach.cloud.client;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by diyanfilipov on 11/2/14.
 */
public class PotlachErrorHandler implements ErrorHandler{
    private RetrofitError error;

    @Override
    public Throwable handleError(RetrofitError cause) {
        error = cause;
        return error.getCause();
    }

    public RetrofitError getError() {
        return error;
    }
}
