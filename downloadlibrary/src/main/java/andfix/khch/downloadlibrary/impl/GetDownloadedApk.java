package andfix.khch.downloadlibrary.impl;

import java.io.File;

/**
 * Created by kuanghaochuan on 16/7/20.
 */
public interface GetDownloadedApk {
    String getDownloadedApkPath();

    void deleteDownloadedFiles();

    int getDownloadedApkVersionCode();

    long getDownloadedApkSize();

    String getDownloadedApkMd5();

    File getDownloadedApk();
}
