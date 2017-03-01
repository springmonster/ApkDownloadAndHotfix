package com.wisetv.networklibrary;


import com.wisetv.networklibrary.builder.WTNetworkConfig;
import com.wisetv.networklibrary.log.WTLog;
import com.wisetv.networklibrary.request.OkHttpWTRequestCall;
import com.wisetv.networklibrary.request.VolleyWTRequestCall;
import com.wisetv.networklibrary.request.WTRequestCall;
import com.wisetv.networklibrary.request.WTRequestManager;

/**
 * Created by kuanghaochuan on 2017/2/17.
 */

public class WTNetworkManager {
    private volatile static WTNetworkManager sWTNetworkManager;
    private WTNetworkConfig mWTNetworkConfig;

    private WTNetworkManager(WTNetworkConfig WTNetworkConfig) {
        if (WTNetworkConfig == null) {
            throw new NullPointerException("Network Config can't be null");
        } else {
            initConfig(WTNetworkConfig);
        }
    }

    public static WTNetworkManager initNetwork(WTNetworkConfig WTNetworkConfig) {
        if (sWTNetworkManager == null) {
            synchronized (WTNetworkManager.class) {
                if (sWTNetworkManager == null) {
                    sWTNetworkManager = new WTNetworkManager(WTNetworkConfig);
                }
            }
        }
        return sWTNetworkManager;
    }

    private void initConfig(WTNetworkConfig wtNetworkConfig) {
        this.mWTNetworkConfig = wtNetworkConfig;
        WTLog.setIsLoggable(wtNetworkConfig.isLoggable());
        generateRequestCallFactory(wtNetworkConfig);
    }

    private void generateRequestCallFactory(WTNetworkConfig wtNetworkConfig) {
        new WTRequestManager(wtNetworkConfig);
    }

    private WTRequestCall generateWTRequestCall() {
        WTRequestCall wtRequestCall;
        if (WTNetworkConfig.REQUEST_FRAMEWORK_OKHTTP == mWTNetworkConfig.getRequestFramework()) {
            wtRequestCall = new OkHttpWTRequestCall(mWTNetworkConfig.getContext());
        } else {
            wtRequestCall = new VolleyWTRequestCall(mWTNetworkConfig.getContext());
        }
        return wtRequestCall;
    }

    private WTRequestCall.Builder generateWTRequestCallBuilder() {
        WTRequestCall wtRequestCall = generateWTRequestCall();
        return new WTRequestCall.Builder(wtRequestCall)
                .headers(mWTNetworkConfig.getHeaders())
                .timeout(mWTNetworkConfig.getTimeout());
    }

    public WTRequestCall.Builder get() {
        return generateWTRequestCallBuilder().setMethod(WTRequestCall.METHOD.GET);
    }

    public WTRequestCall.Builder post() {
        return generateWTRequestCallBuilder().setMethod(WTRequestCall.METHOD.POST);
    }
}
