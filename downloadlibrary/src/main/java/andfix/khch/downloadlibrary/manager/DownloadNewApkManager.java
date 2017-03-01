package andfix.khch.downloadlibrary.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import andfix.khch.downloadlibrary.R;
import andfix.khch.downloadlibrary.activity.AlertDialogActivity;
import andfix.khch.downloadlibrary.bean.ResponseBean;
import andfix.khch.downloadlibrary.networkrequest.RequestApkInfo;
import andfix.khch.downloadlibrary.utils.DownloadGlobalUtil;
import andfix.khch.downloadlibrary.utils.NetworkUtil;


/**
 * Created by kuanghaochuan on 16/7/20.
 */
public class DownloadNewApkManager {
    public static final String AUTO = "AUTO";
    public static final String RESPONSE_BEAN = "RESPONSE_BEAN";
    private static final String TAG = "DownloadService DownloadNewApkManager";
    private static volatile DownloadNewApkManager sDownloadNewApkManager;

    private DownloadNewApkManager(Context context, Activity activity) {
        DownloadGlobalUtil.sApplicationContext = context;
        DownloadGlobalUtil.sActivity = activity;
    }

    public static DownloadNewApkManager getInstance(Context context, Activity activity) {
        if (sDownloadNewApkManager == null) {
            synchronized (DownloadNewApkManager.class) {
                if (sDownloadNewApkManager == null) {
                    sDownloadNewApkManager = new DownloadNewApkManager(context, activity);
                }
            }
        }
        return sDownloadNewApkManager;
    }

    private static boolean isWifiConnected() {
        return NetworkUtil.checkWifiNetwork(DownloadGlobalUtil.sApplicationContext);
    }

    public void checkUpdate(boolean isAuto, final String requestURL, RequestApkInfo.RequestApkInfoCallback requestApkInfoCallback) {
        if (!isAuto) {
            Toast.makeText(DownloadGlobalUtil.sApplicationContext,
                    DownloadGlobalUtil.sApplicationContext.getResources().getString(R.string.download_apk_toast_checking),
                    Toast.LENGTH_LONG).show();
        }
        if (isWifiConnected()) {
            new RequestApkInfo(requestURL, requestApkInfoCallback).request();
        }
    }

    public void showUpdateDialog(ResponseBean responseBean) {
        Intent intent = new Intent(DownloadGlobalUtil.sApplicationContext, AlertDialogActivity.class);
        intent.putExtra(RESPONSE_BEAN, responseBean);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DownloadGlobalUtil.sApplicationContext.startActivity(intent);
    }
}