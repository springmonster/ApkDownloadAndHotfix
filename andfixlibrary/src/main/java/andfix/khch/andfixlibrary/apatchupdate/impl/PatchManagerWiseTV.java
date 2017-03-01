package andfix.khch.andfixlibrary.apatchupdate.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.alipay.euler.andfix.util.FileUtil;

import java.io.File;
import java.io.IOException;

import andfix.khch.andfixlibrary.apatchupdate.log.AFLog;
import andfix.khch.andfixlibrary.apatchupdate.util.MD5Builder;

/**
 * Created by kuanghaochuan on 2016/6/1.
 */
public class PatchManagerWiseTV extends andfix.khch.andfixlibrary.apatchupdate.impl.PatchManagerDecorator {
    public PatchManagerWiseTV(Context context) {
        super(context);
    }

    /**
     * 如果本地没有补丁文件，设置flag为init
     * 或者在下载补丁完成后，设置flag为init
     * 在加载之前设置flag为crash，如果成功加载设置为none
     * 当为init或者none的时候加载补丁，可能产生一次崩溃
     */
    @Override
    public void loadPatch() {
        try {
            preValidate(super.mAppVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getCrashFlag() == null || getCrashFlag().equals(CRASH_NONE)) {
            AFLog.d(TAG, "get crash flag none");
            saveCrashFlag(CRASH_HAPPEN);
            super.mPatchManagerDecorator.loadPatch();
        }
        AFLog.d(TAG, "get crash flag is " + getCrashFlag());
    }

    private void preValidate(String appVersion) throws IOException {
        if (!isPatchDirExisted()) {
            return;
        }

        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
        String ver = sp.getString(SP_VERSION, null);
        if (ver == null || !ver.equalsIgnoreCase(appVersion)) {
            cleanPatch();
            sp.edit().putString(SP_VERSION, appVersion).commit();
            if (getCrashFlag() != null && getCrashFlag().equals(CRASH_HAPPEN)) {
                // 如果发生过崩溃，则以后再也不加载补丁
            } else {
                saveCrashFlag(null);
            }
        } else {
            initPatch();
        }
    }

    private boolean isPatchDirExisted() {
        if (!mPatchDir.exists() && !mPatchDir.mkdirs()) {// make directory fail
            return false;
        } else if (!mPatchDir.isDirectory()) {// not directory
            mPatchDir.delete();
            return false;
        }
        return true;
    }

    private void initPatch() throws IOException {
        File[] files = mPatchDir.listFiles();

        if (files.length == 0) {
            copyPatchToDataDirectory(mDownloadFile);
        } else {
            if (!checkFileMD5AndSize(files[0]) && checkFileMD5AndSize(mDownloadFile)) {
                copyPatchToDataDirectory(mDownloadFile);
            }
        }
    }

    private boolean checkFileMD5AndSize(File patchFile) {
        AFLog.d(TAG, "apatch file : " + patchFile.getAbsolutePath());
        if (!patchFile.exists()) {
            return false;
        }
        String patchFileMD5 = MD5Builder.getMD5(patchFile);
        AFLog.d(TAG, "apatch file md5: " + patchFileMD5);

        String spMD5 = getFileMD5();
        AFLog.d(TAG, "apatch HOTPATCH_MD5:" + spMD5);

        return patchFileMD5.equalsIgnoreCase(spMD5);
    }

    private void cleanPatch() {
        File[] files = mPatchDir.listFiles();
        for (File file : files) {
            FileUtil.deleteFile(file);
        }
    }

    private void copyPatchToDataDirectory(File srcFile) throws IOException {
        File dest = new File(mPatchDir, srcFile.getName());
        if (!srcFile.exists()) {
            return;
        }

        if (!checkFileMD5AndSize(srcFile)) {
            FileUtil.deleteFile(srcFile);
            return;
        }

        FileUtil.copyFile(srcFile, dest);// copy to patch's directory
        FileUtil.deleteFile(srcFile);
    }
}
