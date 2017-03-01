package andfix.khch.downloadlibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import andfix.khch.downloadlibrary.R;
import andfix.khch.downloadlibrary.bean.ResponseBean;
import andfix.khch.downloadlibrary.log.DLog;
import andfix.khch.downloadlibrary.manager.DownloadNewApkManager;
import andfix.khch.downloadlibrary.service.DownloadService;
import andfix.khch.downloadlibrary.utils.DownloadGlobalUtil;
import andfix.khch.downloadlibrary.utils.PreferencesUtils;


public class AlertDialogActivity extends Activity {
    private static final String TAG = "DownloadService AlertDialogActivity";
    private Button mBtnConfirm;
    private Button mBtnCancel;
    private CheckBox mIgnoreCB;
    private TextView mTextViewLastestVersion;
    private TextView mTextViewLastestVersionSize;
    private TextView mTextViewUpdateInfo;
    private boolean isIgnore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        mBtnConfirm = (Button) findViewById(R.id.alert_dialog_confirm);
        mBtnCancel = (Button) findViewById(R.id.alert_dialog_cancel);
        mIgnoreCB = (CheckBox) findViewById(R.id.ignore_checkbox);
        mTextViewLastestVersion = (TextView) findViewById(R.id.alert_dialog_latest_version);
        mTextViewLastestVersionSize = (TextView) findViewById(R.id.alert_dialog_latest_version_size);
        mTextViewUpdateInfo = (TextView) findViewById(R.id.alert_dialog_update_info);
        mTextViewUpdateInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        Intent intent = getIntent();
        final ResponseBean responseBean = (ResponseBean) intent.getSerializableExtra(DownloadNewApkManager.RESPONSE_BEAN);

        setInfo(R.string.download_apk_alert_latest_version
                , responseBean.getApkVersionName(),
                mTextViewLastestVersion);

        setInfo(R.string.download_apk_alert_latest_version_size
                , getApkSizeFormat(responseBean.getApkLength()),
                mTextViewLastestVersionSize);

        setInfo(R.string.download_apk_alert_update_info,
                responseBean.getApkDescription(),
                mTextViewUpdateInfo);

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloadService(responseBean);
                AlertDialogActivity.this.finish();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isIgnore) {
                    PreferencesUtils.saveIgnoreAppUpdateVersion(DownloadGlobalUtil.sApplicationContext,
                            responseBean.getApkVersionName(), responseBean.getApkVersionCode(), true);
                }
                AlertDialogActivity.this.finish();
            }
        });

        mIgnoreCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isIgnore = isChecked;
            }
        });
    }

    private String getApkSizeFormat(String strSize) {
        int size = Integer.parseInt(strSize);
        return String.valueOf(size / 1024 / 1024);
    }

    private void setInfo(int stringId, String info, TextView textView) {
        String format = DownloadGlobalUtil.sApplicationContext.getResources().getString(stringId);
        String result = String.format(format, info);
        textView.setText(result);
    }

    private void startDownloadService(ResponseBean responseBean) {
        DLog.d(TAG, "start ------> start download service");
        Intent intent = new Intent(DownloadGlobalUtil.sApplicationContext, DownloadService.class);
        intent.putExtra(DownloadNewApkManager.RESPONSE_BEAN, responseBean);
        DownloadGlobalUtil.sApplicationContext.startService(intent);
    }
}
