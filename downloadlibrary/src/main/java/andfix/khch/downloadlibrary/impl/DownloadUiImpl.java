package andfix.khch.downloadlibrary.impl;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import andfix.khch.downloadlibrary.R;
import andfix.khch.downloadlibrary.utils.DownloadGlobalUtil;


/**
 * Created by kuanghaochuan on 16/7/20.
 */
public class DownloadUiImpl implements DownloadUi {
    private static final String TAG = "DownloadService DownloadUiImpl";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private String mFormat;

    @Override
    public void init() {
        mFormat = DownloadGlobalUtil.sApplicationContext.getResources().getString(R.string.download_apk_downloading);

        Intent resultIntent = new Intent(DownloadGlobalUtil.sApplicationContext, DownloadGlobalUtil.sActivity.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(DownloadGlobalUtil.sApplicationContext, 0, resultIntent, 0);

        mNotifyManager = (NotificationManager) DownloadGlobalUtil.sApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(DownloadGlobalUtil.sApplicationContext);
        mBuilder.setContentTitle(DownloadGlobalUtil.sApplicationContext.getResources().getString(R.string.download_apk_title))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
    }

    @Override
    public void setProgress(long max, long progress) {
        mBuilder.setProgress((int) max, (int) progress, false);
        mBuilder.setContentText(DownloadGlobalUtil.getFormattedPercent(mFormat, (float) progress / (float) max * 100));
        mNotifyManager.notify(0, mBuilder.build());
    }

    @Override
    public void completed() {
        if (mBuilder == null && mNotifyManager == null) {
            return;
        }
        mBuilder.setContentText(DownloadGlobalUtil.sApplicationContext.getResources().getString(R.string.download_apk_completed))
                .setProgress(0, 0, false);
        mNotifyManager.notify(0, mBuilder.build());
    }

    @Override
    public void destroy() {
        mNotifyManager.cancel(0);
    }
}
