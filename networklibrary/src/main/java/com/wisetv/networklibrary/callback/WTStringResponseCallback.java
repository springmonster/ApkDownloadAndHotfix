package com.wisetv.networklibrary.callback;

/**
 * Created by kuanghaochuan on 2017/2/20.
 */

public abstract class WTStringResponseCallback implements WTResponseCallback<String> {
    protected abstract void onStringResponseSuccess(String s);

    protected abstract void onStringResponseError(Throwable throwable);

    @Override
    public void onWTResponseCallbackError(Throwable throwable) {
        onStringResponseError(throwable);
    }

    @Override
    public void onWTResponseCallbackSuccess(String s) {
        onStringResponseSuccess(s);
    }
}
