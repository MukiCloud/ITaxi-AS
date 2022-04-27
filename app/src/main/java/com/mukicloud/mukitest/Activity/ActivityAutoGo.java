package com.mukicloud.mukitest.Activity;

import static com.mukicloud.mukitest.TD.PKG_ITaxi_Beta;
import static com.mukicloud.mukitest.TD.PKG_ITaxi_Prod;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.mukicloud.mukitest.SFunc.SMethods;
import com.mukicloud.mukitest.TD;

import java.util.ArrayList;

public class ActivityAutoGo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String AppID = getApplicationInfo().packageName;
        ArrayList<String> PermissionAL = new ArrayList<>();
        if (AppID.contains(PKG_ITaxi_Beta) || AppID.contains(PKG_ITaxi_Prod)) {
            PermissionAL.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        CheckPermission(PermissionAL);
    }

    private void SonCreate() {
        Intent intent = getIntent();
        intent.setClass(this, ActivityWeb.class);
        startActivity(intent);
        finish();
    }

    private void CheckPermission(ArrayList<String> RequestPermissionAL) {
        boolean AllGranted = true;
        for (String Permission : RequestPermissionAL) {
            if (ActivityCompat.checkSelfPermission(ActivityAutoGo.this, Permission) != PackageManager.PERMISSION_GRANTED) {
                AllGranted = false;
            }
        }

        if (AllGranted) {
            SonCreate();
        } else {
            ActivityCompat.requestPermissions(ActivityAutoGo.this, RequestPermissionAL.toArray(new String[0]), TD.RQC_Permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TD.RQC_Permission) {
            if (grantResults.length > 0) {
                boolean AllGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        AllGranted = false;
                        break;
                    }
                }
                if (AllGranted) {
                    SonCreate();
                } else {
                    new SMethods(this).SWToast("請允許全部權限", "您必須先允許App功能必要權限", SweetAlertDialog.WARNING_TYPE);
                    new Handler().postDelayed(this::finish, 3000);
                }
            }
        }
    }
}
