package com.mukicloud.mukitest.SFunc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.mukicloud.mukitest.BuildConfig;
import com.mukicloud.mukitest.R;

import org.json.JSONObject;

/**
 * Created by SinyoTsai on 2017/11/2.
 */

public class SUpdater {
    private Activity Act;
    private SMethods SM;
    private static final String ApkName = "Muki";

    public SUpdater(Activity Act) {
        this.Act = Act;
        SM = new SMethods(Act);
    }

    public void MVersionChecker(JSONObject ResDataJOB) {
        new Handler(Looper.getMainLooper()).post(() -> {
            int android_version = SM.JSONIntGetter(ResDataJOB, "android_version", 0);//App版本
            int android_update = SM.JSONIntGetter(ResDataJOB, "android_update", 0);//強制更新
            String android_apk_url = SM.JSONStrGetter(ResDataJOB, "android_apk_url");//APK網址
            String update_content = SM.JSONStrGetter(ResDataJOB, "update_content");//強制更新文字
            String update_btn = SM.JSONStrGetter(ResDataJOB, "update_btn");//強制更新按鈕
            if (android_version > BuildConfig.VERSION_CODE) {//Run Update
                if (android_apk_url.length() > 0) {
                    //ProgramUpdater(android_version, android_update == 1, android_apk_url, update_content);//Direct Download
                } else {//Play
                    SweetAlertDialog SAD = new SweetAlertDialog(Act, SweetAlertDialog.WARNING_TYPE);
                    SAD.setTitleText("發現新版本");
                    if (update_content.length() > 0) {
                        SAD.showContentText(true);
                        SAD.setContentText(update_content);
                    }
                    SAD.setCancelable(false);
                    SAD.setCanceledOnTouchOutside(false);
                    SAD.showCancelButton(true);
                    SAD.setConfirmText(update_btn.length() > 0 ? update_btn : SM.IDStr(R.string.CM_Update));
                    SAD.setCancelText(SM.IDStr(R.string.CM_Cancel));
                    SAD.setConfirmClickListener(sweetAlertDialog -> {
                        String appPackageName = Act.getPackageName();
                        try {
                            Act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException e) {
                            Act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        } finally {
                            Act.finish();//跳轉後結束App
                        }
                    });
                    SAD.setCancelClickListener(sweetAlertDialog -> {
                        SAD.dismissWithAnimation();
                        if (android_update == 1) Act.finish();//強制更新 取消則結束App
                    });
                    SAD.show();
                }
            }
        });
    }

//    private void ProgramUpdater(int APPVersion, boolean ForceUpdate, String APKUrl, String UpdateContent) {
//        new Handler(Looper.getMainLooper()).post(() -> {
//            try {
//                UpdateConfiguration UC = new UpdateConfiguration();
//                UC.setForcedUpgrade(ForceUpdate);
//                //Start Download
//                DownloadManager manager = DownloadManager.getInstance(Act);
//                manager.setApkName(ApkName + ".apk")
//                        .setApkVersionCode(APPVersion)
//                        .setApkDescription(UpdateContent)
//                        .setConfiguration(UC)
//                        .setApkUrl(APKUrl)
//                        .setSmallIcon(R.drawable.ic_app)
//                        .download();
//            } catch (Exception e) {
//                SM.EXToast(R.string.ERR_PrepareData, "ProgramUpdater", e);
//            }
//        });
//    }
}
