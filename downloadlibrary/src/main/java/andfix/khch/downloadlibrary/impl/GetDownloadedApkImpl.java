package andfix.khch.downloadlibrary.impl;


import java.io.File;

import andfix.khch.downloadlibrary.utils.ApkUtil;
import andfix.khch.downloadlibrary.utils.MD5Builder;

/**
 * Created by kuanghaochuan on 16/7/20.
 */
public class GetDownloadedApkImpl implements GetDownloadedApk {
    private String mApkName;

    public GetDownloadedApkImpl(String apkName) {
        this.mApkName = apkName + ".apk";
    }

    @Override
    public String getDownloadedApkPath() {
        return ApkUtil.getDownloadedApkPath(mApkName);
    }

    @Override
    public void deleteDownloadedFiles() {
        ApkUtil.deleteAllApks();
    }

    @Override
    public int getDownloadedApkVersionCode() {
        return ApkUtil.getDownloadApkVersionCode(mApkName);
    }

    @Override
    public long getDownloadedApkSize() {
        return ApkUtil.getDownloadedApkSize(mApkName);
    }

    @Override
    public String getDownloadedApkMd5() {
        return MD5Builder.getMD5(ApkUtil.getDownloadedApk(mApkName));
    }

    @Override
    public File getDownloadedApk() {
        return ApkUtil.getDownloadedApk(mApkName);
    }
}
