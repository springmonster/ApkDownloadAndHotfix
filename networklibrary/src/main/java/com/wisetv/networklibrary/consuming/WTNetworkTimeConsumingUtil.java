package com.wisetv.networklibrary.consuming;

import java.util.concurrent.TimeUnit;

/**
 * Created by kuanghaochuan on 2017/2/5.
 */

class WTNetworkTimeConsumingUtil {
    private long mStartTime;
    private long mEndTime;

    private long getCurrentTime() {
        return System.nanoTime();
    }

    public long getStartTime() {
        mStartTime = getCurrentTime();
        return TimeUnit.NANOSECONDS.toMillis(mStartTime);
    }

    public long getEndTime() {
        mEndTime = getCurrentTime();
        return TimeUnit.NANOSECONDS.toMillis(mEndTime);
    }

    public long getTimeSpan() {
        long tmpTime = mEndTime - mStartTime;
        long result = tmpTime <= 0 ? 0 : tmpTime;
        return TimeUnit.NANOSECONDS.toMillis(result);
    }
}
