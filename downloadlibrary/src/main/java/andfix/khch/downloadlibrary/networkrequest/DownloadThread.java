package andfix.khch.downloadlibrary.networkrequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import andfix.khch.downloadlibrary.bean.DownloadBean;
import andfix.khch.downloadlibrary.impl.DownloadUi;
import andfix.khch.downloadlibrary.impl.DownloadUiImpl;
import andfix.khch.downloadlibrary.log.DLog;
import andfix.khch.downloadlibrary.utils.DownloadGlobalUtil;
import andfix.khch.downloadlibrary.utils.NetworkUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by kuanghaochuan on 16/8/5.
 */
public class DownloadThread {
    private static final int TIME_OUT = 5000;
    private static final String GET = "GET";
    private static final int READ_SIZE = 1024 * 1024; //1M
    private static final String TAG = "DownloadService DownloadThread";
    private volatile boolean isWifiAvailable = true;
    private DownloadUi mDownloadUi;
    private DownloadThreadCallback mDownloadThreadCallBack;
    private BroadcastReceiver mListenNetStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                isWifiAvailable = NetworkUtil.checkWifiNetwork(DownloadGlobalUtil.sApplicationContext);
            }
        }
    };

    public DownloadThread(DownloadThreadCallback downloadThreadCallback) {
        mDownloadUi = new DownloadUiImpl();
        mDownloadThreadCallBack = downloadThreadCallback;

        registerListenNetStatus();
    }

    public void execute(final DownloadBean downloadBean) {
        rxDownloadStart(downloadBean);
    }

    private void rxDownloadStart(final DownloadBean downloadBean) {
        DLog.d(TAG, "rx download start");
        mDownloadUi.init();

        Observable
                .just(downloadBean)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<DownloadBean, Observable<DownloadBean>>() {
                    @Override
                    public Observable<DownloadBean> call(final DownloadBean downloadBean) {
                        return Observable.create(new Observable.OnSubscribe<DownloadBean>() {
                            @Override
                            public void call(Subscriber<? super DownloadBean> subscriber) {
                                realDownload(subscriber, downloadBean);
                            }
                        });
                    }
                })
                .subscribe(new Subscriber<DownloadBean>() {
                    @Override
                    public void onCompleted() {
                        DLog.d(TAG, "rx download completed");
                        mDownloadUi.completed();
                        mDownloadUi.destroy();
                        unregisterListenNetStatus();
                        mDownloadThreadCallBack.callback(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DLog.e(TAG, e.toString());
                        mDownloadUi.destroy();
                        unregisterListenNetStatus();
                        mDownloadThreadCallBack.callback(false);
                    }

                    @Override
                    public void onNext(DownloadBean downloadBean) {
                        mDownloadUi.setProgress(downloadBean.getServerApkSize(),
                                downloadBean.getProgress());
                    }
                });
    }

    private void realDownload(Subscriber<? super DownloadBean> subscriber, DownloadBean downloadBean) {
        DLog.d(TAG, "real download");
        DLog.d(TAG, "download bean " + downloadBean.toString());
        HttpURLConnection connection = null;
        RandomAccessFile randomAccessFile = null;
        InputStream is = null;

        try {
            randomAccessFile = new RandomAccessFile(downloadBean.getDownloadedApk(), "rwd");
            randomAccessFile.seek(downloadBean.getDownloadedApkSize());

            URL url = new URL(downloadBean.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.setRequestMethod(GET);

            // 从上次下载的位置开始读取
            connection.setRequestProperty("Range", "bytes="
                    + downloadBean.getDownloadedApkSize() + "-");
            DLog.e(TAG, "downloaded apk size " + downloadBean.getDownloadedApkSize() + " server apk size " +
                    downloadBean.getServerApkSize());
            is = connection.getInputStream();
            byte[] buffer = new byte[READ_SIZE];
            int length;
            long lengthSum = downloadBean.getDownloadedApkSize();
            while ((length = is.read(buffer)) != -1) {
                if (isWifiAvailable) {
                    lengthSum += length;
                    downloadBean.setProgress(lengthSum);
                    randomAccessFile.write(buffer, 0, length);
                    subscriber.onNext(downloadBean);
                } else {
                    subscriber.onError(new Throwable("wifi not available"));
                }
            }
            DLog.d(TAG, "download completed <------");
            subscriber.onCompleted();
            // 如果下载完成,准备安装
        } catch (IOException e) {
            DLog.e(TAG, "download error #########");
            DLog.e(TAG, e.toString());
            e.printStackTrace();
            subscriber.onError(new Throwable("read apk stream error !!!"));
        } finally {
            DLog.d(TAG, "server apk download completed");
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerListenNetStatus() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        DownloadGlobalUtil.sApplicationContext.registerReceiver(mListenNetStatusReceiver, mFilter);
    }

    private void unregisterListenNetStatus() {
        DownloadGlobalUtil.sApplicationContext.unregisterReceiver(mListenNetStatusReceiver);
    }

    public interface DownloadThreadCallback {
        void callback(boolean install);
    }
}
