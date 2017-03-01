package andfix.khch.downloadlibrary.utils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.IOException;

import andfix.khch.downloadlibrary.log.DLog;

/**
 * Created by kuanghaochuan on 16/7/20.
 */
public class ApkUtil {
    private static final String TAG = "DownloadService ApkUtil";
    private static final String FOLDER_NAME = "wiseApk";

    /**
     * 根据文件路径获取文件大小,原始大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long fileSize = -1;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            fileSize = file.length();
        }
        return fileSize;
    }

    /**
     * 得到存储的路径
     *
     * @return
     */
    public static String getStoragePath() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                Environment.getExternalStorageDirectory().toString() :
                "/tmp";
    }

    /**
     * 得到当前剩余存储空间
     *
     * @return
     */
    public static long getHandPhoneStorageAvailableSize() {
        String path = getStoragePath();
        StatFs stat = new StatFs(path);
        long blockSize;    // 获得一个扇区的大小
        long availableBlocks;    // 获得可用的扇区数量
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }

        return blockSize * availableBlocks * 1024 * 1024;
    }

    /**
     * 得到存放apk的路径，/mnt/sdcard/wiseApk/
     *
     * @return
     */
    public static File getDownloadedApkStorageDirectory() {
        String dirPath = getStoragePath();
        File destDir = null;
        if (dirPath != null) {
            destDir = new File(dirPath + File.separator + FOLDER_NAME);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
        }
        return destDir;
    }

    /**
     * 得到已经下载的apk
     *
     * @return
     */
    public static File getDownloadedApk(final String apkName) {
        File file = new File(getDownloadedApkStorageDirectory() + File.separator + apkName);
        if (file.exists()) {
            return file;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 得到已经下载的apk的大小
     *
     * @return
     */
    public static long getDownloadedApkSize(final String apkName) {
        return getFileSize(getDownloadedApk(apkName).getPath());
    }

    /**
     * 得到已经下载的apk的路径
     *
     * @return
     */
    public static String getDownloadedApkPath(final String apkName) {
        return getDownloadedApk(apkName).getPath();
    }

    /**
     * 删除已经下载的apk文件
     *
     * @return
     */
    public static void deleteAllApks() {
        File file = getDownloadedApkStorageDirectory();
        File[] files = file.listFiles();
        for (File file1 :
                files) {
            if (!file1.isDirectory()) {
                file1.delete();
            }
        }
    }

    /**
     * 获取已经下载的apk的versionCode
     *
     * @param apkName
     * @return versionCode
     */
    public static int getDownloadApkVersionCode(String apkName) {
        final PackageManager pm = DownloadGlobalUtil.sApplicationContext.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(getDownloadedApkPath(apkName), 0);
        return info.versionCode;
    }

    /**
     * 获取已经安装的apk的versionCode
     *
     * @return versionCode
     */
    public static int getInstalledAppVersionCode() {
        int result = 0;
        PackageManager manager = DownloadGlobalUtil.sApplicationContext.getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(DownloadGlobalUtil.sApplicationContext.getPackageName(), 0);
            result = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(String filePath) {
        DLog.d(TAG, "apk location " + filePath);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + filePath),
                "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DownloadGlobalUtil.sApplicationContext.startActivity(i);
    }
}
