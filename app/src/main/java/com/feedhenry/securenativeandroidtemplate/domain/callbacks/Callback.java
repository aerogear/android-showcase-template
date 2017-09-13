package com.feedhenry.securenativeandroidtemplate.domain.callbacks;

/**
 * Represents the callback functions to be invoken when async functions are finished.
 */

public interface Callback<T extends Object> {
    public void onSuccess(T models);

    public void onError(Throwable error);
}
