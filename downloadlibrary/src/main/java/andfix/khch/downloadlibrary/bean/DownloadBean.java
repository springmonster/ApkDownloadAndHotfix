package andfix.khch.downloadlibrary.bean;

import java.io.File;

/**
 * Created by kuanghaochuan on 16/7/13.
 */
public class DownloadBean {
    private String mUrl;
    private long mProgress;
    private long mServerApkSize;
    private long mDownloadedApkSize;
    private File mDownloadedApk;

    public DownloadBean(String url, long serverApkSize, long progress, long downloadedApkSize, File downloadedApk) {
        mUrl = url;
        mServerApkSize = serverApkSize;
        mProgress = progress;
        mDownloadedApkSize = downloadedApkSize;
        mDownloadedApk = downloadedApk;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public long getDownloadedApkSize() {
        return mDownloadedApkSize;
    }

    public File getDownloadedApk() {
        return mDownloadedApk;
    }

    public long getServerApkSize() {
        return mServerApkSize;
    }

    public long getProgress() {
        return mProgress;
    }

    public void setProgress(long progress) {
        mProgress = progress;
    }

    @Override
    public String toString() {
        return "DownloadBean{" +
                "mUrl='" + mUrl + '\'' +
                ", mProgress=" + mProgress +
                ", mServerApkSize=" + mServerApkSize +
                ", mDownloadedApkSize=" + mDownloadedApkSize +
                ", mDownloadedApk=" + mDownloadedApk +
                '}';
    }
}
