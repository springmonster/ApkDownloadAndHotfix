package andfix.khch.downloadlibrary.bean;

import java.io.File;
import java.io.Serializable;

/**
 * Created by kuanghaochuan on 16/7/13.
 */
public class ResponseBean implements Serializable {

    /**
     * code : 200
     * apkVersionCode : 111
     * apkVersionName : 111
     * apkDescription : 1、------
     * 2、修复bugs
     * apkLength : 11821479
     * apkUrl : xxx.apk
     * apkMD5 : d5f5a73f64019ccf401280fcd6378968
     */

    private String code;
    private String apkVersionCode;
    private String apkVersionName;
    private String apkDescription;
    private String apkLength;
    private String apkUrl;
    private String apkMD5;
    private boolean mNeedToDownload = true;
    private long mDownloadedApkSize;
    private File mDownloadedApk;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getApkVersionCode() {
        return apkVersionCode;
    }

    public void setApkVersionCode(String apkVersionCode) {
        this.apkVersionCode = apkVersionCode;
    }

    public String getApkVersionName() {
        return apkVersionName;
    }

    public void setApkVersionName(String apkVersionName) {
        this.apkVersionName = apkVersionName;
    }

    public String getApkDescription() {
        return apkDescription;
    }

    public void setApkDescription(String apkDescription) {
        this.apkDescription = apkDescription;
    }

    public String getApkLength() {
        return apkLength;
    }

    public void setApkLength(String apkLength) {
        this.apkLength = apkLength;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getApkMD5() {
        return apkMD5;
    }

    public void setApkMD5(String apkMD5) {
        this.apkMD5 = apkMD5;
    }

    public boolean isNeedToDownload() {
        return mNeedToDownload;
    }

    public void setNeedToDownload(boolean needToDownload) {
        mNeedToDownload = needToDownload;
    }

    public long getDownloadedApkSize() {
        return mDownloadedApkSize;
    }

    public void setDownloadedApkSize(long downloadedApkSize) {
        mDownloadedApkSize = downloadedApkSize;
    }

    public File getDownloadedApk() {
        return mDownloadedApk;
    }

    public void setDownloadedApk(File downloadedApk) {
        mDownloadedApk = downloadedApk;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "code='" + code + '\'' +
                ", apkVersionCode='" + apkVersionCode + '\'' +
                ", apkVersionName='" + apkVersionName + '\'' +
                ", apkDescription='" + apkDescription + '\'' +
                ", apkLength='" + apkLength + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                ", apkMD5='" + apkMD5 + '\'' +
                ", mNeedToDownload=" + mNeedToDownload +
                ", mDownloadedApkSize=" + mDownloadedApkSize +
                ", mDownloadedApk=" + mDownloadedApk +
                '}';
    }
}