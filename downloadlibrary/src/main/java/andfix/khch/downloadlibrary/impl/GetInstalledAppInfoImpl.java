package andfix.khch.downloadlibrary.impl;


import andfix.khch.downloadlibrary.utils.ApkUtil;

/**
 * Created by kuanghaochuan on 16/8/5.
 */
public class GetInstalledAppInfoImpl implements GetInstalledAppInfo {
    @Override
    public int getInstalledAppVersionCode() {
        return ApkUtil.getInstalledAppVersionCode();
    }
}
