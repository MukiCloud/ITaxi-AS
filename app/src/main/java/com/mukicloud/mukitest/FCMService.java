package com.mukicloud.mukitest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mukicloud.mukitest.Activity.ActivityAutoGo;
import com.mukicloud.mukitest.SFunc.SMethods;

import org.json.JSONObject;

import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by SinyoTsai on 2017/10/24.
 */

public class FCMService extends FirebaseMessagingService {
    private final FCMService SVC = this;
    private SMethods SM;

    @Override
    public void onNewToken(@NonNull String Token) {
        super.onNewToken(Token);
        SM = new SMethods(SVC.getBaseContext());
        SM.SPSaveStringData("token", Token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage RM) {
        super.onMessageReceived(RM);
        try {
            if (SM == null) SM = new SMethods(SVC.getBaseContext());
            RemoteMessage.Notification NF = RM.getNotification();
            Map<String, String> FCMMap = RM.getData();
            if (FCMMap.size() > 0) {
                String Title = MapGetter(FCMMap, "title");
                String Body = MapGetter(FCMMap, "body");
                String Url = MapGetter(FCMMap, "url_link");
                String Vibrate = MapGetter(FCMMap, "vibrate");
                String Sound = MapGetter(FCMMap, "sound");
                String Badge = MapGetter(FCMMap, "badge");
                //如果 Title Body Sound 沒資料使用Notification
                if (NF != null) {
                    if (Title.length() == 0) Title = NF.getTitle();
                    if (Body.length() == 0) Body = NF.getBody();
                    if (Sound.length() == 0) Sound = NF.getSound();
                }
                //相容信鴿
                try {
                    if (FCMMap instanceof androidx.collection.ArrayMap) {
                        androidx.collection.ArrayMap<String, String> XGMap = (androidx.collection.ArrayMap<String, String>) FCMMap;
                        JSONObject XG_JOB = SM.JOBGetter(XGMap.get("content"));
                        Title = GetXGData(XG_JOB, Title, "title");
                        Body = GetXGData(XG_JOB, Body, "content");
                        Vibrate = GetXGData(XG_JOB, Vibrate, "vibrate").equals("1") ? "True" : "False";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Title != null && Title.length() > 0) {
                    SendNotification(SVC, new NFHolder(Title, Body, Url, Vibrate, Sound, Badge));
                }
                //Badge=======================
                int SPBadgeNum = SM.StI(SM.SPReadStringData("BadgeNum"));
                int ReceiveBadgeNum = SM.StI(Badge, -1);
                if (ReceiveBadgeNum > 0) {
                    SPBadgeNum = ReceiveBadgeNum;
                    ShortcutBadger.applyCount(SVC, ReceiveBadgeNum); //for 1.1.4+
                } else if (ReceiveBadgeNum == 0) {
                    SPBadgeNum = ReceiveBadgeNum;
                    ShortcutBadger.removeCount(SVC); //for 1.1.4+
                } else {
                    SPBadgeNum++;
                    ShortcutBadger.applyCount(SVC, SPBadgeNum); //for 1.1.4+
                }
                SM.SPSaveStringData("BadgeNum", String.valueOf(SPBadgeNum));
                //JS回傳訊息
                sendFCMServiceBroadCast(new JSONObject(FCMMap));
            } else {
                if (NF != null) SendNotification(NF);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String GetXGData(JSONObject XG_JOB, String Content, String ID) {
        String Value = SM.JSONStrGetter(XG_JOB, ID);
        if (Value != null && Value.length() > 0) {
            Content = Value;
        }
        return Content;
    }

    public static class NFHolder {
        int NotifyID;
        String Title, Body, Url, Vibrate, Sound, Badge;

        public NFHolder(String title, String body, String url, String vibrate, String sound, String badge) {
            NotifyID = (int) (Math.random() * 20);
            Title = title;
            Body = body;
            Url = url;
            Vibrate = vibrate;
            Sound = sound;
            Badge = badge;
        }
    }

    public static final String NOTIFICATION_CHANNEL_NAME = "Muki";

    private void SendNotification(RemoteMessage.Notification NF) {
        String Title = NF.getTitle();
        String Body = NF.getBody();
        String Sound = NF.getSound();
        String Vibrate = NF.getDefaultVibrateSettings() ? "True" : "False";
        SendNotification(SVC, new NFHolder(Title, Body, "", Vibrate, Sound, ""));
    }

    public static void SendNotification(Context Con, NFHolder NFH) {
        if (Con == null) return;
        String NOTIFICATION_CHANNEL_ID = Con.getResources().getString(R.string.default_notification_channel_id);
        Uri SoundUri = GetSound(Con, NFH);//SoundAlert

        int SDKVersion = android.os.Build.VERSION.SDK_INT;
        NotificationManager notificationManager = (NotificationManager) Con.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && SDKVersion >= android.os.Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);//Delete
            //Create New
            AudioAttributes Att = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build();//USAGE_NOTIFICATION

            NotificationChannel notificationChannel = PrepareNotificationChannel(Con);
            notificationChannel.setSound(SoundUri, Att);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        Intent intent = new Intent(Con, ActivityAutoGo.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction("Notify");
        intent.putExtra("GoUrl", NFH.Url);
//        PendingIntent pendingIntent = PendingIntent.getActivity(Con, NFH.NotifyID, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(Con, NFH.NotifyID, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(Con, NFH.NotifyID, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(Con, NFH.NotifyID, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(Con, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon((SDKVersion >= Build.VERSION_CODES.LOLLIPOP && SDKVersion < Build.VERSION_CODES.N) ? R.drawable.ic_app : R.drawable.ic_app_white)
                .setContentTitle(NFH.Title)
                .setContentText(NFH.Body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(NFH.Body))
                .setTicker(NFH.Title)
                .setAutoCancel(true)
                .setSound(SoundUri)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent, true);

        if (notificationManager != null) {
            notificationManager.notify(NFH.NotifyID, notificationBuilder.build());
        }
    }

    private String MapGetter(Map<String, String> FCMMap, String Key) {
        if (FCMMap != null) {
            String Value = FCMMap.get(Key);
            return Value != null ? Value : "";
        }
        return "";
    }

    private static Uri GetSound(Context Con, NFHolder NFH) {
        String ApplicationID = Con.getApplicationInfo().packageName;
        //Get Default Sound
        Uri SoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (ApplicationID.contains("freecome")) {
            SoundUri = Uri.parse("android.resource://" + Con.getPackageName() + "/" + R.raw.money);
        }
        return SoundUri;
    }

    private void sendFCMServiceBroadCast(JSONObject ValueJOB) {
        try {
            if (ValueJOB != null) {
                SM.SPSaveStringData("FCMValueJOB", ValueJOB.toString());
                Intent intent = new Intent("FCMService");
                intent.putExtra("ValueJOB", ValueJOB.toString());
                sendBroadcast(intent);
            }
        } catch (Exception e) {
            SM.EXToast(R.string.ERR_ProcessData, "sendFCMServiceBroadCast", e);
        }
    }

    public static NotificationChannel PrepareNotificationChannel(Context Con) {
        NotificationChannel notificationChannel = null;
        int SDKVersion = android.os.Build.VERSION.SDK_INT;
        NotificationManager notificationManager = (NotificationManager) Con.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && SDKVersion >= android.os.Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = Con.getResources().getString(R.string.default_notification_channel_id);
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
        }
        return notificationChannel;
    }
}