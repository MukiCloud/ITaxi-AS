package com.mukicloud.mukitest.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mukicloud.mukitest.FCMService;
import com.mukicloud.mukitest.SFunc.SMethods;

import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.mukicloud.mukitest.FCMService.SendNotification;

public class PushyReceiver extends BroadcastReceiver {
    private SMethods SM;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SM == null) SM = new SMethods(context);
        String Title = GetIntentVal(intent, "title");
        String Body = GetIntentVal(intent, "body");
        String Url = GetIntentVal(intent, "url");
        String Vibrate = GetIntentVal(intent, "vibrate");
        String Sound = GetIntentVal(intent, "sound");
        String Badge = GetIntentVal(intent, "badge");


        SendNotification(context, new FCMService.NFHolder(Title, Body, Url, Vibrate, Sound, Badge));
        //Badge=======================
        int SPBadgeNum = SM.StI(SM.SPReadStringData("BadgeNum"));
        int ReceiveBadgeNum = SM.StI(Badge, -1);
        if (ReceiveBadgeNum > 0) {
            SPBadgeNum = ReceiveBadgeNum;
            ShortcutBadger.applyCount(context, ReceiveBadgeNum); //for 1.1.4+
        } else if (ReceiveBadgeNum == 0) {
            SPBadgeNum = ReceiveBadgeNum;
            ShortcutBadger.removeCount(context); //for 1.1.4+
        } else {
            SPBadgeNum++;
            ShortcutBadger.applyCount(context, SPBadgeNum); //for 1.1.4+
        }
        SM.SPSaveStringData("BadgeNum", String.valueOf(SPBadgeNum));
    }

    private String GetIntentVal(Intent intent, String Key) {
        if (intent != null && intent.getStringExtra(Key) != null) {
            return intent.getStringExtra(Key);
        }
        return "";
    }
}
