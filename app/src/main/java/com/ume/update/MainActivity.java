package com.ume.update;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppUpdateManager.getInstance().checkUpdate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
