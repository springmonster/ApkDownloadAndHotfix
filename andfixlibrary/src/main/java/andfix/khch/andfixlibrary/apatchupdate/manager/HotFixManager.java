package andfix.khch.andfixlibrary.apatchupdate.manager;

import android.content.Context;

import andfix.khch.andfixlibrary.apatchupdate.impl.PatchManagerDownload;
import andfix.khch.andfixlibrary.apatchupdate.impl.PatchManagerProxy;
import andfix.khch.andfixlibrary.apatchupdate.impl.PatchManagerWiseTV;
import andfix.khch.andfixlibrary.apatchupdate.util.GlobalUtil;

/**
 * Created by kuanghaochuan on 2016/5/26.
 * Description:
 * 1.采用AndFix中PatchManger的大版本校验机制，如果发布新版本，则旧版本的所有.apatch文件作废
 * 2.如果data下面的.apatch文件不存在，则校验sd卡存在的.apatch文件的md5和size与下载成功时保存的md5和size是否相同，防止篡改
 * 2.1 如果相同，则复制sd卡的.apatch文件到data下并在sd卡上删除
 * 2.2 如果不相同，删除sd卡的.apatch文件
 * 3.如果data下面的.apatch文件存在，
 * 3.1 如果校验data.apatch的md5和size不相同（表明下载的sd.apatch为新版本），
 * 校验sd.apatch的md5和size相同（防止篡改或者删除），
 * 同时满足则复制sd卡的.apatch文件到data下并在sd卡上删除
 * 3.2 如果不满足则继续使用data下的.apatch文件
 * 4.使用AndFix的PatchManger进行初始化，以为data下面只会有最新的.apatch文件或者没有，此时只需loadPatch即可
 */
public class HotFixManager {
    private static volatile HotFixManager sHotFixManager;
    private static Context sContext;

    private HotFixManager() {
    }

    public static HotFixManager getInstance() {
        if (sHotFixManager == null) {
            synchronized (HotFixManager.class) {
                if (sHotFixManager == null) {
                    sHotFixManager = new HotFixManager();
                }
            }
        }
        return sHotFixManager;
    }

    public void init(HotFixConfig hotFixConfig) {
        sContext = hotFixConfig.mContext;
        GlobalUtil.setIsLoggable(hotFixConfig.isLoggable);
        GlobalUtil.setRequestUrl(hotFixConfig.mRequestUrl);

        PatchManagerProxy patchManagerProxy = new PatchManagerProxy(sContext);
        PatchManagerWiseTV patchManagerWiseTV = new PatchManagerWiseTV(sContext);
        PatchManagerDownload patchManagerDownload = new PatchManagerDownload(sContext);

        patchManagerWiseTV.setPatchManagerDecorator(patchManagerProxy);
        patchManagerDownload.setPatchManagerDecorator(patchManagerWiseTV);
        patchManagerDownload.loadPatch();
    }
}
