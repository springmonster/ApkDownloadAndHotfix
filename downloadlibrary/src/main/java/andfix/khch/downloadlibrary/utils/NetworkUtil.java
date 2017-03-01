package andfix.khch.downloadlibrary.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by kuanghaochaun on 2017/03/01.
 */
public final class NetworkUtil {
    /**
     * 检测手机是否开启WIFI网络,需要调用ConnectivityManager服务.
     *
     * @param
     */
    public static boolean checkWifiNetwork(Context context) {
        boolean has = false;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info == null) {
            return has;
        }
        int netType = info.getType();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            has = info.isConnected();
        }
        return has;
    }
}
