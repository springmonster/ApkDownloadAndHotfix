package andfix.khch.andfixlibrary.apatchupdate.impl;

import android.content.Context;

import com.alipay.euler.andfix.patch.PatchManager;

import andfix.khch.andfixlibrary.apatchupdate.log.AFLog;

/**
 * Created by kuanghaochuan on 2016/5/30.
 */
public class PatchManagerProxy extends andfix.khch.andfixlibrary.apatchupdate.impl.PatchManagerDecorator {

    public PatchManagerProxy(Context context) {
        super(context);
    }

    @Override
    public void loadPatch() {
        try {
            AFLog.d(TAG, "andfix real fix start");
            PatchManager mPatchManager = new PatchManager(super.mContext);
            mPatchManager.init(super.mAppVersion);
            mPatchManager.loadPatch();
            saveCrashFlag(CRASH_NONE);
            AFLog.d(TAG, "andfix real fix end and no crash");
        } catch (Exception e) {
            AFLog.e(TAG, "andfix fix error");
            e.printStackTrace();
        }
    }
}
