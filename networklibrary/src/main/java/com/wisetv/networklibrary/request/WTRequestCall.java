package com.wisetv.networklibrary.request;

import android.text.TextUtils;

import com.wisetv.networklibrary.callback.WTPreActionCallback;
import com.wisetv.networklibrary.callback.WTStringResponseCallback;
import com.wisetv.networklibrary.consuming.WTNetworkTimeConsuming;
import com.wisetv.networklibrary.consuming.WTNetworkTimeConsumingPrinter;
import com.wisetv.networklibrary.log.WTLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kuanghaochuan on 2017/2/17.
 */

public abstract class WTRequestCall {
    protected static final String TAG = "WTRequestCall";
    protected int mMethod;
    protected String mRequestUrl;
    protected Map<String, String> mHeaders;
    protected Map<String, String> mBodyParams;
    protected int mTimeout;
    protected WTPreActionCallback mWTPreActionCallback;
    private WTNetworkTimeConsuming mWTNetworkTimeConsuming;

    protected void executePreAction() {
        if (mWTPreActionCallback != null) {
            mWTPreActionCallback.onPreAction();
        }
    }

    protected void createTimeConsuming() {
        if (WTLog.isLoggable()) {
            mWTNetworkTimeConsuming = new WTNetworkTimeConsumingPrinter(this.mRequestUrl);
        }
    }

    protected void showTimeConsuming(boolean success, String responseResult) {
        if (mWTNetworkTimeConsuming != null) {
            mWTNetworkTimeConsuming.getTimeSpan(success, responseResult);
        }
    }

    public abstract void execute(WTStringResponseCallback wtStringResponseCallback);

    public static class Builder {
        protected int mMethod;
        protected String mRequestUrl;
        protected Map<String, String> mHeaders = new HashMap<>();
        protected Map<String, String> mBodyParams = new HashMap<>();
        protected int mTimeout;
        protected WTPreActionCallback mWTPreActionCallback;
        private WTRequestCall mWTRequestCall;

        public Builder(WTRequestCall wtRequestCall) {
            this.mWTRequestCall = wtRequestCall;
        }

        public Builder setMethod(int method) {
            mMethod = method;
            return this;
        }

        public Builder params(String key, String value) {
            mBodyParams.put(key, value);
            return this;
        }

        public Builder params(Map<String, String> bodyParams) {
            mBodyParams = bodyParams;
            return this;
        }

        public Builder headers(String key, String value) {
            mHeaders.put(key, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            mHeaders = headers;
            return this;
        }

        public Builder url(String requestUrl) {
            if (TextUtils.isEmpty(requestUrl)) {
                throw new NullPointerException("URL can't be empty");
            }
            mRequestUrl = requestUrl;
            return this;
        }

        public Builder timeout(int timeout) {
            if (timeout >= 0) {
                mTimeout = timeout;
            }
            return this;
        }

        public Builder preAction(WTPreActionCallback WTPreActionCallback) {
            mWTPreActionCallback = WTPreActionCallback;
            return this;
        }

        public WTRequestCall create() {
            applyConfig(this.mWTRequestCall);
            return this.mWTRequestCall;
        }

        private void applyConfig(WTRequestCall wtRequestCall) {
            wtRequestCall.mMethod = mMethod;
            wtRequestCall.mRequestUrl = mRequestUrl;
            wtRequestCall.mHeaders = mHeaders;
            wtRequestCall.mBodyParams = mBodyParams;
            wtRequestCall.mTimeout = mTimeout;
            wtRequestCall.mWTPreActionCallback = mWTPreActionCallback;
        }
    }

    public static class METHOD {
        public static final int GET = 0;
        public static final int POST = 1;
    }
}
