package andfix.khch.andfixlibrary.apatchupdate.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kuanghaochuan on 2017/2/16.
 */

public class PreferencesUtils {
    private static final String TAG = "PreferencesUtils";
    public static String PREFERENCE_NAME = "andfix.khch.andfixlibrary";

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }
}
