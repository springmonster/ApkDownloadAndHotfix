package com.wisetv.networklibrary.log;

import android.util.Log;

/**
 * Created by kuanghaochuan on 2017/2/17.
 */

public class WTLog {
    private static boolean isLoggable = true;

    public static void setIsLoggable(boolean isLoggable) {
        WTLog.isLoggable = isLoggable;
    }

    public static boolean isLoggable() {
        return isLoggable;
    }

    public static void v(String tag, String msg) {
        if (isLoggable) {
            Log.v(tag, msg);
        }
    }


    public static void v(String tag, String msg, Throwable tr) {
        if (isLoggable) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (isLoggable) {
            Log.d(tag, msg);
        }
    }


    public static void d(String tag, String msg, Throwable tr) {
        if (isLoggable) {
            Log.d(tag, msg, tr);
        }
    }


    public static void i(String tag, String msg) {
        if (isLoggable) {
            Log.i(tag, msg);
        }
    }


    public static void i(String tag, String msg, Throwable tr) {
        if (isLoggable) {
            Log.i(tag, msg, tr);
        }

    }

    public static void w(String tag, String msg) {
        if (isLoggable) {
            Log.w(tag, msg);
        }
    }


    public static void w(String tag, String msg, Throwable tr) {
        if (isLoggable) {
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (isLoggable) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isLoggable) {
            Log.e(tag, msg, tr);
        }
    }
}
