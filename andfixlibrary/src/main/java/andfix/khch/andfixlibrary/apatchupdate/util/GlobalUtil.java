package andfix.khch.andfixlibrary.apatchupdate.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import andfix.khch.andfixlibrary.apatchupdate.log.AFLog;

/**
 * Created by kuanghaochuan on 2017/2/16.
 */

public class GlobalUtil {
    private static final String TAG = "GlobalUtil";
    private static String sRequestUrl;
    private static boolean isLoggable;

    public static int getVersionCode(Context context) {
        int version = 0;
        try {
            version = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionCode;
        } catch (Exception e) {
            AFLog.e(TAG, "getVersionCode", e);
        }
        return version;
    }

    public static int getInstalledAppVersionCode(Context context) {
        int result = 0;
        PackageManager manager = context.getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            result = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public static void setRequestUrl(final String url) {
        sRequestUrl = url;
    }

    public static String getRequestUrl() {
        return sRequestUrl;
    }

    public static boolean isLoggable() {
        return isLoggable;
    }

    public static void setIsLoggable(boolean isLoggable) {
        GlobalUtil.isLoggable = isLoggable;
    }
}
