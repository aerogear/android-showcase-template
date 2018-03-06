package com.feedhenry.securenativeandroidtemplate.domain.callbacks;

/**
 * Represents the callback functions to be invoken when async functions are finished.
 */

public interface CallbackHandler<T extends Object> {
    default void onSuccess() {}
    default void onSuccess(T models) {onSuccess();}
    void onError(Throwable error);
}
