package com.ume.update.component;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ume.update.AppUpdateManager;
import com.ume.update.R;
import com.ume.update.model.ApkInfo;
import com.ume.update.utils.LogUtils;
import com.ume.update.utils.Utils;

public class UpdateDialogActivity extends AppCompatActivity {

    public static final String KEY_EXTRA_APK = "KEY_EXTRA_APK";
    private TextView mFeatureTextView;
    private TextView mSizeTextView;
    private TextView mVersionTextView;
    private ApkInfo mApkInfo;

    public static void showDialogActivity(Context context, ApkInfo apkInfo) {
        Intent intent = new Intent(context, UpdateDialogActivity.class);
        intent.putExtra(KEY_EXTRA_APK, apkInfo);
        context.startActivity(intent);
    }

    private void getData() {
        mApkInfo = getIntent().getParcelableExtra(KEY_EXTRA_APK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dialog);
        initView();
        getData();
        setData();
        setFullScreen();
    }

    private void setFullScreen() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void setData() {
        if (null != mApkInfo) {
            if (!TextUtils.isEmpty(mApkInfo.getVersionName())) {
                mVersionTextView.setText(mApkInfo.getVersionName());
            }
            if (!TextUtils.isEmpty(mApkInfo.getFeature())) {
                mFeatureTextView.setText(mApkInfo.getFeature());
            }
            if (!TextUtils.isEmpty(mApkInfo.getSize())) {
                mSizeTextView.setText(mApkInfo.getSize());
            }
        }
    }

    private void initView() {
        mFeatureTextView = (TextView) findViewById(R.id.tv_update_info);
        mSizeTextView = (TextView) findViewById(R.id.tv_update_size);
        mVersionTextView = (TextView) findViewById(R.id.tv_update_version);
    }

    public void onDownload(View view) {
        if (null != mApkInfo) {
            String downUrl = mApkInfo.getDownUrl();
            LogUtils.i("Donald", downUrl);
            if (Utils.checkFile(this, mApkInfo.getVersionName()) ) {
                Utils.openDownloadFile(getApplicationContext(), mApkInfo.getVersionName());
            } else {
                if (!TextUtils.isEmpty(mApkInfo.getDownUrl())) {
                    AppUpdateManager.getManager().bindUpdateService(mApkInfo);
                }
            }
            finish();
        }
    }

    public void onClose(View view) {
        finish();
    }
}
