package andfix.khch.andfixinproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import andfix.khch.andfixlibrary.apatchupdate.manager.HotFixConfig;
import andfix.khch.andfixlibrary.apatchupdate.manager.HotFixManager;
import andfix.khch.downloadlibrary.bean.ResponseBean;
import andfix.khch.downloadlibrary.manager.DownloadNewApkManager;
import andfix.khch.downloadlibrary.networkrequest.RequestApkInfo;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.andfix_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAndFix();
            }
        });

        findViewById(R.id.download_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
    }

    private void startAndFix() {
        String url = "";

        HotFixConfig hotFixConfig = new HotFixConfig.Builder()
                .setContext(MainActivity.this.getApplicationContext())
                .setLoggable(true)
                .setRequestUrl(url)
                .create();

        HotFixManager.getInstance().init(hotFixConfig);
    }

    private void startDownload() {
        String requestURL = "";

        final DownloadNewApkManager downloadNewApkManager = DownloadNewApkManager.getInstance(this, this);

        downloadNewApkManager.checkUpdate(false, requestURL, new RequestApkInfo.RequestApkInfoCallback() {
            @Override
            public void callback(int result, ResponseBean responseBean) {
                if (result == 0 && responseBean != null) {
                    downloadNewApkManager.showUpdateDialog(responseBean);
                }
            }
        });
    }
}
