package andfix.khch.downloadlibrary.utils;

import android.widget.Toast;

/**
 * Created by kuanghaochuan on 2017/3/1.
 */

public class ToastUtil {
    public static void showMsg(int resourceId) {
        Toast.makeText(DownloadGlobalUtil.sApplicationContext,
                DownloadGlobalUtil.sApplicationContext.getResources().getString(resourceId),
                Toast.LENGTH_LONG).show();
    }
}
