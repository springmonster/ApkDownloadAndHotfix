package andfix.khch.andfixlibrary.apatchupdate.log;

import android.util.Log;

import andfix.khch.andfixlibrary.apatchupdate.util.GlobalUtil;

/**
 * Created by kuanghaochuan on 2017/2/16.
 */

public class AFLog {
    private static final boolean isLoggable = GlobalUtil.isLoggable();

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
