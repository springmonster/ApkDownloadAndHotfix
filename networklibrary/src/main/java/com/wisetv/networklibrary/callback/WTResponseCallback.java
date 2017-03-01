package com.wisetv.networklibrary.callback;

public interface WTResponseCallback<T> {
    void onWTResponseCallbackSuccess(T t);

    void onWTResponseCallbackError(Throwable throwable);
}