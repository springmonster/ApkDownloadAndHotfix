package andfix.khch.andfixlibrary.apatchupdate.impl;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import andfix.khch.andfixlibrary.apatchupdate.util.GlobalUtil;
import andfix.khch.andfixlibrary.apatchupdate.util.PreferencesUtils;

/**
 * Created by kuanghaochuan on 2016/6/1.
 */
public abstract class PatchManagerDecorator {
    static final String TAG = "HotFixManager";

    static final String HOTPATCH_MD5 = "HOTPATCH_MD5";

    static final String CRASH_FLAG = "CRASH_FLAG";
    static final String CRASH_NONE = "1";
    static final String CRASH_HAPPEN = "-1";

    static final String DIR = "apatch";
    static final String SP_NAME = "_andfix_";
    static final String SP_VERSION = "version";
    static final String DOWNLOAD_RESPONSE_SUCCESS_CODE = "200";
    static final String CLIENT_APK_VERSION_CODE = "clientApkVersionCode";

    static String PATCH_NAME;
    PatchManagerDecorator mPatchManagerDecorator;
    Context mContext;
    String mAppVersion;
    File mDownloadFile;
    File mPatchDir;
    String mDownloadFilePatch;

    public PatchManagerDecorator(Context context) {
        this.mContext = context;
        this.PATCH_NAME = GlobalUtil.getInstalledAppVersionCode(this.mContext) + "wisetv.apatch";
        this.mAppVersion = String.valueOf(GlobalUtil.getVersionCode(this.mContext));
        this.mPatchDir = new File(mContext.getFilesDir(), DIR);
        this.mDownloadFilePatch = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + TAG;
        File file = new File(this.mDownloadFilePatch);
        if (!file.exists()) {
            file.mkdir();
        }
        this.mDownloadFile = new File(this.mDownloadFilePatch + File.separator + PATCH_NAME);
    }

    public void setPatchManagerDecorator(PatchManagerDecorator patchManagerDecorator) {
        this.mPatchManagerDecorator = patchManagerDecorator;
    }

    protected void saveFileMD5(String md5) {
        PreferencesUtils.putString(mContext, HOTPATCH_MD5, md5);
    }

    protected String getFileMD5() {
        return PreferencesUtils.getString(mContext, HOTPATCH_MD5);
    }

    protected void saveCrashFlag(String crashFlag) {
        PreferencesUtils.putString(mContext, CRASH_FLAG, crashFlag);
    }

    protected String getCrashFlag() {
        return PreferencesUtils.getString(mContext, CRASH_FLAG);
    }

    public abstract void loadPatch();
}
