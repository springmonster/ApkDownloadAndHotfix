package andfix.khch.downloadlibrary.utils;

import android.app.Activity;
import android.content.Context;

import java.text.NumberFormat;

/**
 * Created by kuanghaochuan on 16/8/7.
 */
public class DownloadGlobalUtil {
    public static Context sApplicationContext = null;
    public static Activity sActivity = null;

    private static NumberFormat sNumberFormat;

    static {
        sNumberFormat = NumberFormat.getNumberInstance();
        sNumberFormat.setMaximumFractionDigits(0);
    }

    public static String getFormattedPercent(String format, float value) {
        String result = String.format(format, sNumberFormat.format(value) + "%");
        return result;
    }
}
