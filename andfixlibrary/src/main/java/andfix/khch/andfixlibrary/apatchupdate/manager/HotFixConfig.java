package andfix.khch.andfixlibrary.apatchupdate.manager;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by kuanghaochuan on 2017/2/16.
 */

public class HotFixConfig {
    protected boolean isLoggable;
    protected String mRequestUrl;
    protected Context mContext;

    private HotFixConfig() {
    }

    public static class Builder {
        Context mContext = null;
        boolean isLoggable = false;
        String mRequestUrl = null;

        public Builder setContext(Context context) {
            if (context == null) {
                throw new NullPointerException();
            }
            mContext = context;
            return this;
        }

        public Builder setLoggable(boolean loggable) {
            isLoggable = loggable;
            return this;
        }

        public Builder setRequestUrl(String requestUrl) {
            if (TextUtils.isEmpty(requestUrl)) {
                throw new NullPointerException();
            }
            mRequestUrl = requestUrl;
            return this;
        }

        public HotFixConfig create() {
            HotFixConfig hotFixConfig = new HotFixConfig();
            applyConfig(hotFixConfig);
            return hotFixConfig;
        }

        private void applyConfig(HotFixConfig hotFixConfig) {
            hotFixConfig.mContext = this.mContext;
            hotFixConfig.isLoggable = this.isLoggable;
            hotFixConfig.mRequestUrl = this.mRequestUrl;
        }
    }
}
