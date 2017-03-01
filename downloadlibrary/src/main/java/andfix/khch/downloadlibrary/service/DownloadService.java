package andfix.khch.downloadlibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import andfix.khch.downloadlibrary.R;
import andfix.khch.downloadlibrary.bean.DownloadBean;
import andfix.khch.downloadlibrary.bean.ResponseBean;
import andfix.khch.downloadlibrary.impl.GetDownloadedApk;
import andfix.khch.downloadlibrary.impl.GetDownloadedApkImpl;
import andfix.khch.downloadlibrary.impl.GetInstalledAppInfo;
import andfix.khch.downloadlibrary.impl.GetInstalledAppInfoImpl;
import andfix.khch.downloadlibrary.log.DLog;
import andfix.khch.downloadlibrary.manager.DownloadNewApkManager;
import andfix.khch.downloadlibrary.networkrequest.DownloadThread;
import andfix.khch.downloadlibrary.utils.ApkUtil;
import andfix.khch.downloadlibrary.utils.ToastUtil;

/**
 * Created by kuanghaochuan on 16/7/20.
 */
public class DownloadService extends Service implements DownloadThread.DownloadThreadCallback {
    private static final String TAG = "DownloadService";
    private DownloadBean mDownloadBean;
    private boolean isStarted = false;
    private ResponseBean mResponseBean;

    @Override
    public void onCreate() {
        super.onCreate();
        DLog.d(TAG, "DownloadService onCreate");
    }

    @Override
    public void onDestroy() {
        DLog.d(TAG, "DownloadService onDestroy");
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isStarted) {
            isStarted = true;

            DLog.d(TAG, "---> DownloadService onStartCommand <---");

            mResponseBean = (ResponseBean) intent.getSerializableExtra(DownloadNewApkManager.RESPONSE_BEAN);
            setDownloadBean(mResponseBean);

            if (!mResponseBean.isNeedToDownload()) {
                DLog.d(TAG, "local apk existed install downloaded apk");
                installDownloadedApk(true);
            } else {
                new DownloadThread(DownloadService.this).execute(getDownloadBean());
            }
        } else {
            DLog.e(TAG, "during downloading");
            ToastUtil.showMsg(R.string.download_apk_toast_is_downloading);
        }
        return START_NOT_STICKY;
    }

    private DownloadBean getDownloadBean() {
        return mDownloadBean;
    }

    private void setDownloadBean(ResponseBean responseBean) {
        mDownloadBean = new DownloadBean(responseBean.getApkUrl(),
                Long.parseLong(responseBean.getApkLength()),
                0,
                responseBean.getDownloadedApkSize(),
                responseBean.getDownloadedApk());
    }

    private void installDownloadedApk(boolean install) {
        if (install) {
            GetDownloadedApk getDownloadedApk = new GetDownloadedApkImpl(mResponseBean.getApkVersionName());
            GetInstalledAppInfo getInstalledAppInfo = new GetInstalledAppInfoImpl();

            String serverApkMd5 = mResponseBean.getApkMD5();
            String downloadedApkMd5 = getDownloadedApk.getDownloadedApkMd5();

            if (serverApkMd5.equalsIgnoreCase(downloadedApkMd5)) {
                try {
                    int installedApkVersionCode = getInstalledAppInfo.getInstalledAppVersionCode();
                    int downloadedApkVersionCode = getDownloadedApk.getDownloadedApkVersionCode();
                    if (installedApkVersionCode < downloadedApkVersionCode) {
                        ApkUtil.installApk(ApkUtil.getDownloadedApkPath(getDownloadBean().getDownloadedApk().getName()));
                    }
                } catch (Exception e) {
                    ToastUtil.showMsg(R.string.download_apk_toast_apk_error);
                }
            } else {
                ToastUtil.showMsg(R.string.download_apk_toast_apk_error);
            }
        }
        DLog.d(TAG, "---> download server kill <---");
        killService();
    }

    /**
     * {@link DownloadThread#mDownloadThreadCallBack}
     *
     * @param install
     */
    @Override
    public void callback(boolean install) {
        installDownloadedApk(install);
    }

    private void killService() {
        isStarted = false;
        stopSelf();
    }
}
