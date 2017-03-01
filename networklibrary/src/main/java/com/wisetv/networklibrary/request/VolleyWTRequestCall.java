package com.wisetv.networklibrary.request;

import android.content.Context;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.wisetv.networklibrary.callback.WTResponseCallback;
import com.wisetv.networklibrary.callback.WTStringResponseCallback;
import com.wisetv.networklibrary.log.WTLog;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by kuanghaochuan on 2017/2/17.
 */

public class VolleyWTRequestCall extends WTRequestCall {
    private WTResponseCallback<String> mStringWTResponseCallback;
    private Handler mHandler;

    public VolleyWTRequestCall(Context context) {
        WTLog.d(TAG, "VolleyWTRequestCall create");
        mHandler = new Handler(context.getMainLooper());
    }

    @Override
    public void execute(WTStringResponseCallback wtStringResponseCallback) {
        super.executePreAction();

        createTimeConsuming();

        mStringWTResponseCallback = wtStringResponseCallback;

        final VolleyStringRequest volleyStringRequest = new VolleyStringRequest(super.mMethod, super.mRequestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                showTimeConsuming(true, response);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mStringWTResponseCallback != null) {
                            mStringWTResponseCallback.onWTResponseCallbackSuccess(response);
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                showTimeConsuming(false, error.getMessage());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mStringWTResponseCallback != null) {
                            mStringWTResponseCallback.onWTResponseCallbackError(error);
                        }
                    }
                });

            }
        });

        WTRequestManager.getVolleyQueue().add(volleyStringRequest);
    }

    private class VolleyStringRequest extends StringRequest {
        public VolleyStringRequest(int method, String url, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
            super(method, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    listener.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorListener.onErrorResponse(error);
                }
            });
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            String resultStr = null;
            try {
                resultStr = new String(response.data, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return Response.success(resultStr, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            headers.putAll(mHeaders);
            return headers;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> bodyParams = new HashMap<>();
            bodyParams.putAll(mBodyParams);
            return bodyParams;
        }

        @Override
        public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
            return super.setRetryPolicy(new DefaultRetryPolicy(mTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }
}
