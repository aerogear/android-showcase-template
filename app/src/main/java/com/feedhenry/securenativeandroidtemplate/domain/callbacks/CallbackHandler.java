package com.feedhenry.securenativeandroidtemplate.domain.callbacks;

/**
 * Represents the callback functions to be invoken when async functions are finished.
 */

public interface CallbackHandler<T extends Object> {
    public void onSuccess(T models);

    public void onError(Throwable error);
}
