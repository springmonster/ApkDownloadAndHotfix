package andfix.khch.downloadlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kuanghaochaun on 2017/03/01.
 */
public class PreferencesUtils {
    public static String PREFERENCE_NAME = "com.khch.download";

    public static void saveIgnoreAppUpdateVersion(Context context, String versionName, String versionCode, boolean ignore) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(versionName + "." + versionCode, ignore);
        editor.commit();
    }

}