package com.aerogear.androidshowcase.domain.callbacks;

import java.util.concurrent.ExecutionException;

/**
 * Represents the callback functions to be invoken when async functions are finished.
 */

public interface CallbackHandler<T extends Object> {
    default void onSuccess() {}
    default void onSuccess(T models) throws ExecutionException, InterruptedException {onSuccess();}
    void onError(Throwable error);
}
