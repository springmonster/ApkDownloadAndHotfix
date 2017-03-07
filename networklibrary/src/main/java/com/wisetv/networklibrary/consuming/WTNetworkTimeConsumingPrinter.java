package com.wisetv.networklibrary.consuming;

import android.util.LogPrinter;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.util.Log.DEBUG;

/**
 * Created by kuanghaochuan on 2017/2/5.
 */

public class WTNetworkTimeConsumingPrinter implements WTNetworkTimeConsuming {
    private static final String TAG = "WTNetworkTimeConsuming";
    private WTNetworkTimeConsumingUtil mWTNetworkTimeConsumingUtil;
    private String mUrl;
    private LogPrinter mLog;
    private String mRequestTime;
    private String mResponseTime;

    private String mMethodFirstLine;
    private String mMethodSecondLine;
    private String mMethodThirdLine;

    public WTNetworkTimeConsumingPrinter(final String url) {
        mUrl = url;
        mWTNetworkTimeConsumingUtil = new WTNetworkTimeConsumingUtil();
        init();
    }

    private void init() {
        getStartTime();
        mRequestTime = getCurrentTimeString();
        mLog = new LogPrinter(DEBUG, TAG);
        printStackTrace();
    }

    private void printStackTrace() {
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0, n = stack.length; i < n; i++) {
            StackTraceElement ste = stack[i];
            if (i == 6) {
                mMethodFirstLine = "╔════════════════════════════════════════════════════════════════════════════════";
                mMethodSecondLine = "║ (" + ste.getFileName() + ":" + ste.getLineNumber() + ")#" + ste.getMethodName();
                mMethodThirdLine = "║────────────────────────────────────────────────────────────────────────────────";
                break;
            }
        }
    }

    private String getCurrentTimeString() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }

    public long getStartTime() {
        return mWTNetworkTimeConsumingUtil.getStartTime();
    }

    @Override
    public long getTimeSpan(boolean success, String responseResult) {
        getEndTime();
        mResponseTime = getCurrentTimeString();
        printTimeSpan(success, responseResult);
        return mWTNetworkTimeConsumingUtil.getTimeSpan();
    }

    private long getEndTime() {
        return mWTNetworkTimeConsumingUtil.getEndTime();
    }

    private void printTimeSpan(boolean success, String responseResult) {
        mLog.println(mMethodFirstLine);
        mLog.println(mMethodSecondLine);
        mLog.println(mMethodThirdLine);
        mLog.println("║ Request time    : " + mRequestTime);
        mLog.println("║ Response time   : " + mResponseTime);
        mLog.println("║ Request url     : " + mUrl);
        mLog.println("║ Response status : " + (success ? "success" : "error"));
        mLog.println("║────────────────────────────────────────────────────────────────────────────────");
        mLog.println("║ Time span       : " + mWTNetworkTimeConsumingUtil.getTimeSpan() + "ms");
        mLog.println("║────────────────────────────────────────────────────────────────────────────────");
        mLog.println("║ Response result : " + responseResult);
        mLog.println("╚════════════════════════════════════════════════════════════════════════════════");
    }
}
