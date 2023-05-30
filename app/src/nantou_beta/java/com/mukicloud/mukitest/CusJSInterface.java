package com.mukicloud.mukitest;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.mukicloud.mukitest.SFunc.SMethods;

public class CusJSInterface {
    public SMethods SM;

    public CusJSInterface(Activity Act) {
        SM = new SMethods(Act);
    }

    public void onDestroy() {}

    @JavascriptInterface
    public void Empty(String Title){
        try {
            SM.UIToast("");
        }catch (Exception e){
            SM.EXToast(R.string.CM_DetectError,"Empty",e);
        }
    }
}
