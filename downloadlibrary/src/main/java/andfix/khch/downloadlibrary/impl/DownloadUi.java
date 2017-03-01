package andfix.khch.downloadlibrary.impl;

/**
 * Created by kuanghaochuan on 16/7/20.
 */
public interface DownloadUi {
    void init();

    void setProgress(long max, long progress);

    void completed();

    void destroy();
}
