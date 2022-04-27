package com.mukicloud.mukitest;

import android.app.Application;

public class SApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Init XG
        //StubAppUtils.attachBaseContext(this);
        //Init TBS Core
        //QbSdk.forceSysWebView();
        /*
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean Success) {
                if (Success)
                    Toast.makeText(getApplicationContext(), "Core Init Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCoreInitFinished() {
                //Toast.makeText(getApplicationContext(), "onCoreInitFinished", Toast.LENGTH_SHORT).show();
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);

         */
    }
}
