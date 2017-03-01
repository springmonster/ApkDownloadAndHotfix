package andfix.khch.andfixlibrary.apatchupdate.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.wisetv.networklibrary.WTNetworkManager;
import com.wisetv.networklibrary.builder.WTNetworkConfig;
import com.wisetv.networklibrary.callback.WTStringResponseCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import andfix.khch.andfixlibrary.apatchupdate.bean.ApatchResponseBean;
import andfix.khch.andfixlibrary.apatchupdate.log.AFLog;
import andfix.khch.andfixlibrary.apatchupdate.util.GlobalUtil;
import andfix.khch.andfixlibrary.apatchupdate.util.MD5Builder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by kuanghaochuan on 2016/6/1.
 */
public class PatchManagerDownload extends andfix.khch.andfixlibrary.apatchupdate.impl.PatchManagerDecorator {
    private static final int TIME_OUT = 5000;
    private static final String GET = "GET";
    private static final int READ_SIZE = 1024 * 1024; //1M
    private WTNetworkConfig.Builder mBuilder;

    public PatchManagerDownload(Context context) {
        super(context);

        mBuilder = new WTNetworkConfig.Builder()
                .setContext(context)
                .setLoggable(true)
                .setRequestFramework(WTNetworkConfig.REQUEST_FRAMEWORK_OKHTTP);

    }

    private boolean needToDownload(File patchFile, String serverFileMD5) {
        String localFileMD5;
        if (!patchFile.exists()) {
            localFileMD5 = getFileMD5();
            return localFileMD5 == null || !serverFileMD5.equalsIgnoreCase(localFileMD5);
        } else {
            localFileMD5 = MD5Builder.getMD5(patchFile);
            AFLog.i(TAG, "need to download local file md5 :" + localFileMD5);
            AFLog.i(TAG, "need to download serverFileMD5 file md5 :" + serverFileMD5);
            return !serverFileMD5.equalsIgnoreCase(localFileMD5);
        }
    }

    private void deleteExistedFile(File patchFile) {
        if (patchFile.exists()) {
            patchFile.delete();
        }
    }

    @Override
    public void loadPatch() {
        super.mPatchManagerDecorator.loadPatch();
        requestApatch();
    }

    /**
     * 自行实现网络请求，获取{@link ApatchResponseBean}的信息
     * 这里采用的是{@link HttpURLConnection}
     */
    private void requestApatch() {
        WTNetworkManager.initNetwork(mBuilder.create())
                .post()
                .url(GlobalUtil.getRequestUrl())
                .params(CLIENT_APK_VERSION_CODE, String.valueOf(GlobalUtil.getInstalledAppVersionCode(this.mContext)))
                .create()
                .execute(new WTStringResponseCallback() {
                    @Override
                    public void onStringResponseSuccess(String s) {
                        try {
                            ApatchResponseBean apatchResponseBean = new Gson().fromJson(s, ApatchResponseBean.class);
                            if (apatchResponseBean.getCode().equalsIgnoreCase(DOWNLOAD_RESPONSE_SUCCESS_CODE)) {
                                prepareDownloadApatch(apatchResponseBean);
                            }
                        } catch (Exception e) {
                            prepareDownloadApatch(null);
                        }
                    }

                    @Override
                    public void onStringResponseError(Throwable throwable) {
                        prepareDownloadApatch(null);
                    }
                });
    }

    private void prepareDownloadApatch(final ApatchResponseBean apatchResponseBean) {
        if (apatchResponseBean == null)
            return;
        else {
            if (needToDownload(mDownloadFile, apatchResponseBean.getApatchMD5())) {
                AFLog.i(TAG, "need to download new patch");
                deleteExistedFileAndStartDownload(apatchResponseBean);
            } else {
                AFLog.i(TAG, "downloaded patch can use");
            }
        }
    }

    private void deleteExistedFileAndStartDownload(final ApatchResponseBean apatchResponseBean) {
        deleteExistedFile(mDownloadFile);

        Observable
                .just(apatchResponseBean)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<ApatchResponseBean, Observable<ApatchResponseBean>>() {
                    @Override
                    public Observable<ApatchResponseBean> call(final ApatchResponseBean apatchResponseBean) {
                        return getApatchResponseBeanObservable(apatchResponseBean);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ApatchResponseBean>() {
                    @Override
                    public void onCompleted() {
                        onDownloadCompleted(apatchResponseBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ApatchResponseBean apatchResponseBean) {

                    }
                });
    }

    private void onDownloadCompleted(ApatchResponseBean apatchResponseBean) {
        String serverMD5 = apatchResponseBean.getApatchMD5();
        String downloadedMD5 = MD5Builder.getMD5(mDownloadFile);
        if (serverMD5.equalsIgnoreCase(downloadedMD5)) {
            saveFileMD5(apatchResponseBean.getApatchMD5());
        } else {
            deleteExistedFile(mDownloadFile);
        }
    }

    @NonNull
    private Observable<ApatchResponseBean> getApatchResponseBeanObservable(final ApatchResponseBean apatchResponseBean) {
        return Observable.create(new Observable.OnSubscribe<ApatchResponseBean>() {
            @Override
            public void call(Subscriber<? super ApatchResponseBean> subscriber) {
                realDownload(subscriber, apatchResponseBean);
            }
        });
    }

    private void realDownload(Subscriber<? super ApatchResponseBean> subscriber, ApatchResponseBean apatchResponseBean) {
        AFLog.d(TAG, "real download start and bean " + apatchResponseBean.toString());
        HttpURLConnection connection = null;
        RandomAccessFile randomAccessFile = null;
        InputStream is = null;

        try {
            randomAccessFile = new RandomAccessFile(mDownloadFile, "rwd");

            URL url = new URL(apatchResponseBean.getApatchUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.setRequestMethod(GET);

            // 从上次下载的位置开始读取
            connection.setRequestProperty("Range", "bytes=" + 0 + "-");
            AFLog.e(TAG, "real downloaded apatch size " + 0 + " server apatch size " +
                    apatchResponseBean.getApatchLength());
            is = connection.getInputStream();
            byte[] buffer = new byte[READ_SIZE];
            int length;
            while ((length = is.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, length);
                subscriber.onNext(apatchResponseBean);
            }
            AFLog.d(TAG, "real download completed <------");
            subscriber.onCompleted();
            // 如果下载完成,准备安装
        } catch (IOException e) {
            AFLog.e(TAG, "real download error #########");
            AFLog.e(TAG, e.toString());
            e.printStackTrace();
            subscriber.onError(new Throwable("real download read apatch stream error !!!"));
        } finally {
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
}
