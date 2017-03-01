package com.wisetv.networklibrary.request;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.wisetv.networklibrary.builder.WTNetworkConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;

/**
 * Created by kuanghaochuan on 2017/2/23.
 */

public class WTRequestManager {
    private static RequestQueue sRequestQueue;
    private static OkHttpClient sOkHttpClient;
    private WTNetworkConfig mWTNetworkConfig;
    HurlStack hurlStack = new HurlStack() {
        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {
            try {
                String scheme = url.toURI().getScheme();
                if (scheme.equals("https")) {
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    if (mWTNetworkConfig.getSSLSocketFactory() != null) {
                        httpsURLConnection.setSSLSocketFactory(mWTNetworkConfig.getSSLSocketFactory());
                    }
                    if (mWTNetworkConfig.getHostnameVerifier() != null) {
                        httpsURLConnection.setHostnameVerifier(mWTNetworkConfig.getHostnameVerifier());
                    }
                    return httpsURLConnection;
                } else {
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                    return httpUrlConnection;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    public WTRequestManager(WTNetworkConfig networkConfig) {
        mWTNetworkConfig = networkConfig;
        if (WTNetworkConfig.REQUEST_FRAMEWORK_VOLLEY == networkConfig.getRequestFramework()) {
            createVolleyQueue(networkConfig);
        } else {
            createOkHttpClient(networkConfig);
        }
    }

    public static RequestQueue getVolleyQueue() {
        return sRequestQueue;
    }

    public static OkHttpClient getOkHttpClient() {
        return sOkHttpClient;
    }

    private void createOkHttpClient(WTNetworkConfig networkConfig) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (networkConfig.getSSLSocketFactory() != null) {
            builder.sslSocketFactory(networkConfig.getSSLSocketFactory());
        }
        if (networkConfig.getHostnameVerifier() != null) {
            builder.hostnameVerifier(networkConfig.getHostnameVerifier());
        }

        sOkHttpClient = builder.connectTimeout(networkConfig.getTimeout(), TimeUnit.MILLISECONDS).build();
    }

    private void createVolleyQueue(WTNetworkConfig networkConfig) {
        sRequestQueue = Volley.newRequestQueue(networkConfig.getContext(), hurlStack);
    }
}
