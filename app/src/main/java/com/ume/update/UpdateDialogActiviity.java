package com.ume.update;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.ume.update.model.ApkInfo;

public class UpdateDialogActiviity extends AppCompatActivity {

    public static final String KEY_APKINFO = "KEY_APKINFO";

    public static void showDialogActivity(Context context, ApkInfo apkInfo) {
        Intent intent = new Intent(context, UpdateDialogActiviity.class);
        intent.putExtra(KEY_APKINFO, apkInfo);
        context.startActivity(intent);

    }

    void getData() {

        ApkInfo apkInfo = getIntent().getParcelableExtra(KEY_APKINFO);

    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dialog);
        getData();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
