package andfix.khch.downloadlibrary.networkrequest;

import com.google.gson.GsonBuilder;
import com.wisetv.networklibrary.WTNetworkManager;
import com.wisetv.networklibrary.builder.WTNetworkConfig;
import com.wisetv.networklibrary.callback.WTStringResponseCallback;

import java.util.HashMap;

import andfix.khch.downloadlibrary.bean.ResponseBean;
import andfix.khch.downloadlibrary.impl.GetDownloadedApkImpl;
import andfix.khch.downloadlibrary.impl.GetInstalledAppInfoImpl;
import andfix.khch.downloadlibrary.log.DLog;
import andfix.khch.downloadlibrary.utils.ApkUtil;
import andfix.khch.downloadlibrary.utils.DownloadGlobalUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by kuanghaochuan on 16/8/5.
 */
public class RequestApkInfo {
    private static final String TAG = "DownloadService RequestApkInfo";
    private static final String DOWNLOAD_RESPONSE_SUCCESS_CODE = "200";
    private static final String DOWNLOAD_RESPONSE_EXISTED_CODE = "-1";
    private static final String CLIENT_APK_VERSION_CODE = "clientApkVersionCode";
    //    private static final String DOWNLOADED_NODE_URL = "http://192.168.23.228:9681/v1/apkdownload/";
    private String mRequestURL = "";
    private String mInstalledAppVersionCode;
    private RequestApkInfoCallback mRequestApkInfoCallback;
    private GetInstalledAppInfoImpl mGetInstalledAppInfo;
    private GetDownloadedApkImpl mGetDownloadedApk;

    public RequestApkInfo(String requestURL, RequestApkInfoCallback requestApkInfoCallback) {
        mRequestURL = requestURL;
        mInstalledAppVersionCode = String.valueOf(new GetInstalledAppInfoImpl().getInstalledAppVersionCode());
        mRequestApkInfoCallback = requestApkInfoCallback;
    }

    public void request() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(CLIENT_APK_VERSION_CODE, mInstalledAppVersionCode);

        WTNetworkConfig.Builder mBuilder = new WTNetworkConfig.Builder()
                .setContext(DownloadGlobalUtil.sApplicationContext)
                .setLoggable(true)
                .setHeaders(new HashMap<String, String>())
                .setRequestFramework(WTNetworkConfig.REQUEST_FRAMEWORK_OKHTTP);

        WTNetworkManager.initNetwork(mBuilder.create())
                .post()
                .url(mRequestURL)
                .params(parameters)
                .create()
                .execute(new WTStringResponseCallback() {
                    @Override
                    protected void onStringResponseSuccess(String s) {
                        DLog.d(TAG, "request new apk info : " + s);
                        parseJsonToDataEntity(s);
                    }

                    @Override
                    protected void onStringResponseError(Throwable throwable) {
                        DLog.e(TAG, throwable.toString());
                        mRequestApkInfoCallback.callback(-1, null);
                    }
                });
    }

    private void parseJsonToDataEntity(String s) {
        try {
            ResponseBean responseBean = new GsonBuilder().create().fromJson(s, ResponseBean.class);
            if (responseBean.getCode().equalsIgnoreCase(DOWNLOAD_RESPONSE_SUCCESS_CODE)) {
                checkHasNewVersion(responseBean);
            } else if (responseBean.getCode().equalsIgnoreCase(DOWNLOAD_RESPONSE_EXISTED_CODE)) {
                ApkUtil.deleteAllApks();
                mRequestApkInfoCallback.callback(-1, responseBean);
            }
        } catch (Exception e) {
            DLog.e(TAG, e.toString());
            mRequestApkInfoCallback.callback(-1, null);
        }
    }

    private void checkHasNewVersion(ResponseBean responseBean) {
        Observable
                .just(responseBean)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<ResponseBean, Observable<ResponseBean>>() {
                    @Override
                    public Observable<ResponseBean> call(final ResponseBean responseBean) {
                        return Observable.create(new Observable.OnSubscribe<ResponseBean>() {

                            @Override
                            public void call(Subscriber<? super ResponseBean> subscriber) {
                                compareLocalApkAndServerApk(subscriber, responseBean);
                            }
                        });
                    }
                })
                .subscribe(new Subscriber<ResponseBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DLog.e(TAG, e.toString());
                        mRequestApkInfoCallback.callback(-1, null);
                    }

                    @Override
                    public void onNext(ResponseBean responseBean) {
                        DLog.d(TAG, "on next should show dialog");
                        mRequestApkInfoCallback.callback(0, responseBean);
                    }
                });
    }

    private void compareLocalApkAndServerApk(Subscriber<? super ResponseBean> subscriber, ResponseBean responseBean) {
        mGetInstalledAppInfo = new GetInstalledAppInfoImpl();
        mGetDownloadedApk = new GetDownloadedApkImpl(responseBean.getApkVersionName());

        responseBean.setDownloadedApk(mGetDownloadedApk.getDownloadedApk());
        responseBean.setDownloadedApkSize(mGetDownloadedApk.getDownloadedApkSize());

        DLog.d(TAG, "compareLocalApkAndServerApk");
        DLog.d(TAG, "download bean " + responseBean.toString());
        DLog.d(TAG, "installed app version code " + mGetInstalledAppInfo.getInstalledAppVersionCode());
        DLog.e(TAG, "downloaded app version md5 " + mGetDownloadedApk.getDownloadedApkMd5());
        DLog.e(TAG, "downloaded app version size " + mGetDownloadedApk.getDownloadedApkSize());
        DLog.e(TAG, "downloaded app path " + mGetDownloadedApk.getDownloadedApkPath());

        if (ApkUtil.getHandPhoneStorageAvailableSize() <= Long.parseLong(responseBean.getApkLength())) {
            subscriber.onError(new Throwable("storage not enough"));
        } else {
            // 服务器apk版本大于当前已经安装的app版本
            if (Integer.parseInt(responseBean.getApkVersionCode()) > mGetInstalledAppInfo.getInstalledAppVersionCode()) {
                // 如果服务器apk的md5等于已经下载的apk的md5
                if (responseBean.getApkMD5().equalsIgnoreCase(mGetDownloadedApk.getDownloadedApkMd5())) {
                    // 如果服务器apk的长度等于已经下载的apk的长度
                    if (Long.parseLong(responseBean.getApkLength()) == mGetDownloadedApk.getDownloadedApkSize()) {
                        // 如果当前已经安装的app版本小于已经下载的apk版本，则进行安装
                        DLog.e(TAG, "downloaded app version code " + mGetDownloadedApk.getDownloadedApkVersionCode());
                        if (mGetInstalledAppInfo.getInstalledAppVersionCode() < mGetDownloadedApk.getDownloadedApkVersionCode()) {
                            responseBean.setNeedToDownload(false);
                            DLog.d(TAG, "local has apk <------------------->");
                        } else {
                            DLog.d(TAG, "download new apk ---> downloaded apk version < installed apk version");
                            resetDownloadedApkSize(responseBean);
                        }
                    } else {
                        DLog.d(TAG, "download new apk ---> downloaded apk size != server apk size");
                        resetDownloadedApkSize(responseBean);
                    }
                } else {
                    // 如果已经下载的apk的md5与服务器apk的md5不相等，并且已经下载的apk长度要大于服务器的apk长度，删除重新下载
                    // 否则直接断点续传
                    if (mGetDownloadedApk.getDownloadedApkSize() >= Integer.parseInt(responseBean.getApkLength())) {
                        DLog.e(TAG, "downloaded apk size " + mGetDownloadedApk.getDownloadedApkSize());
                        DLog.e(TAG, "server apk size " + Integer.parseInt(responseBean.getApkLength()));
                        DLog.e(TAG, "downloaded apk size > server apk size");
                        resetDownloadedApkSize(responseBean);
                    }
                }
                subscriber.onNext(responseBean);
            } else {
                // 如果当前已经安装的app版本大于等于服务器apk的版本，删除本地存在的下载过的apk文件
                resetDownloadedApkSize(responseBean);
                DLog.d(TAG, "installed app version code > server apk version code");
                subscriber.onError(new Throwable("server no new version"));
            }
        }
    }

    private void resetDownloadedApkSize(ResponseBean responseBean) {
        responseBean.setDownloadedApkSize(0);
        mGetDownloadedApk.deleteDownloadedFiles();
    }

    public interface RequestApkInfoCallback {
        void callback(int result, ResponseBean responseBean);
    }
}
