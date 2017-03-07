package com.wisetv.networklibrary.request;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.wisetv.networklibrary.callback.WTStringResponseCallback;
import com.wisetv.networklibrary.log.WTLog;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by kuanghaochuan on 2017/2/18.
 */

public class OkHttpWTRequestCall extends WTRequestCall {
    private Handler mHandler;

    public OkHttpWTRequestCall(Context context) {
        WTLog.d(TAG, "OkHttpWTRequestCall create");
        mHandler = new Handler(context.getMainLooper());
    }

    @Override
    public void execute(final WTStringResponseCallback wtStringResponseCallback) {
        super.executePreAction();

        createTimeConsuming();

        Request.Builder builder = setUrlAndHeaders();
        builder = setRequestMethodAndBodyParams(builder);

        OkHttpClient mOkHttpClient = WTRequestManager.getOkHttpClient();

        if (mOkHttpClient.connectTimeoutMillis() != super.mTimeout) {
            mOkHttpClient = WTRequestManager.getOkHttpClient().newBuilder()
                    .connectTimeout(super.mTimeout, TimeUnit.MILLISECONDS)
                    .build();
        }

        mOkHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                showTimeConsuming(false, e.getMessage());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (wtStringResponseCallback != null) {
                            wtStringResponseCallback.onWTResponseCallbackError(e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final boolean isSuccess = response.isSuccessful();
                final String result = response.body().string();
                showTimeConsuming(true, result);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (wtStringResponseCallback != null) {
                            if (isSuccess) {
                                wtStringResponseCallback.onWTResponseCallbackSuccess(result);
                            } else {
                                wtStringResponseCallback.onWTResponseCallbackError(new Throwable(result));
                            }
                        }
                    }
                });
            }
        });
    }

    private Request.Builder setUrlAndHeaders() {
        Headers headers = generateOkHttpHeaders();
        return new Request.Builder().url(super.mRequestUrl).headers(headers);
    }

    private Request.Builder setRequestMethodAndBodyParams(Request.Builder builder) {
        if (super.mMethod == METHOD.GET) {
            builder.get();
        } else if (super.mMethod == METHOD.POST) {
            builder.post(generateOkHttpBodyParams());
        }
        return builder;
    }

    @NonNull
    private Headers generateOkHttpHeaders() {
        Headers.Builder builder = new Headers.Builder();

        Set<Map.Entry<String, String>> entries = mHeaders.entrySet();
        if (entries != null) {
            Iterator iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                builder.add(key, value);
            }
        }
        return builder.build();
    }

    @NonNull
    private RequestBody generateOkHttpBodyParams() {
        FormBody.Builder builder = new FormBody.Builder();

        Set<Map.Entry<String, String>> entries = mBodyParams.entrySet();
        if (entries != null) {
            Iterator iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                builder.add(key, value);
            }
        }
        return builder.build();
    }
}
