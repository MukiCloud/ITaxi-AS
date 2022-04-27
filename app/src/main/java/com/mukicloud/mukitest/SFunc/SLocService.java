package com.mukicloud.mukitest.SFunc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.mukicloud.mukitest.R;
import com.mukicloud.mukitest.TD;

import org.json.JSONObject;


public class SLocService extends Service {
    private static SLocService SVC;
    private static JSONObject TaskJOB;
    private static boolean isArrived = false;
    private SMethods SM;
    //Variables
    private static final int ServiceNotifyID = 100;

    @Override
    public void onCreate() {
        super.onCreate();
        SVC = this;
        SM = new SMethods(SVC);
        StartLocate(SLocService.TaskJOB);
        ShowNotification(SM.IDStr(R.string.app_name));//GPS Service Running);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return START_STICKY;//殺不死方法之一  //return super.onStartCommand(intent, flags, startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);// 停止前台服务
        CurrentLoc = null;
        DistLoc = null;
        TaskJOB = null;
        SVC = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void StartService(Context Con, JSONObject TaskJOB) {
        if (SVC != null) StopService(Con);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    long StartM = System.currentTimeMillis();
                    while (SVC != null) {//Wait until service stop
                        sleep(100);
                        if (System.currentTimeMillis() - StartM > 10000) return;
                    }
                    sleep(1500);//稍等一下其他任務結束
                    isArrived = false;
                    SLocService.TaskJOB = TaskJOB;
                    Intent intent = new Intent(Con, SLocService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Con.startForegroundService(intent);
                    } else {
                        Con.startService(intent);
                    }
                } catch (Exception e) {
                    UIToast(Con, "StartService\n" + e.getMessage());
                }
            }
        }.start();
    }

    public static void StopService(Context Con) {
        try {
            if (SVC != null) {
                LocationManager LM = (LocationManager) Con.getSystemService(Context.LOCATION_SERVICE);
                if (LM != null) {
                    if (LLNet != null) LM.removeUpdates(LLNet);
                    if (LLGps != null) LM.removeUpdates(LLGps);
                }
                Con.stopService(new Intent(Con, SLocService.class));
                SWakeLock(Con, false, 0);
                TimeOutHandler(Con, false);
            }
        } catch (Exception e) {
            UIToast(Con, "StopService\n" + e.getMessage());
        }
    }

    //Location======================================================================================
    private static SLocationListener LLNet, LLGps;
    private Location CurrentLoc, DistLoc;

    public void StartLocate(JSONObject TaskJOB) {
        try {
            int LocMode = SM.JSONIntGetter(TaskJOB, "LocMode", 0);//使用模式 預設 GPS + A-GPS
            int coordinateCondDiff = SM.JSONIntGetter(TaskJOB, "coordinateCondDiff");//離目標距離多少公尺內進行ReturnUrl
            int coordinateCondTiming = SM.JSONIntGetter(TaskJOB, "coordinateCondTiming", 240);//多久後中止回傳資料 , 單位是"分" 預設240分鐘
            int coordinateCondTime = SM.JSONIntGetter(TaskJOB, "coordinateCondTime", 10);// 每隔幾秒回傳資料
            //Get DistLocation
            DistLoc = new Location(LocationManager.GPS_PROVIDER);
            DistLoc.setLatitude(SM.JSONDoubleGetter(TaskJOB, "coordinateCondLatitude"));
            DistLoc.setLongitude(SM.JSONDoubleGetter(TaskJOB, "coordinateCondLongitude"));
            LLNet = new SLocationListener(coordinateCondDiff);
            LLGps = new SLocationListener(coordinateCondDiff);
            //Start Listen GPS
            if (ActivityCompat.checkSelfPermission(SVC, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager LM = (LocationManager) SVC.getSystemService(Context.LOCATION_SERVICE);
                if (LM != null) {
                    int UpdateFreq = coordinateCondTime * 1000;
                    if (LocMode == 0) {//Both
                        LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, UpdateFreq, 1, LLGps);
                        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UpdateFreq, 1, LLNet);
                        CurrentLoc = LM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (CurrentLoc == null)
                            CurrentLoc = LM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    } else if (LocMode == 1) {//GPS
                        LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, UpdateFreq, 1, LLGps);
                        CurrentLoc = LM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    } else if (LocMode == 2) {//Network
                        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UpdateFreq, 1, LLNet);
                        CurrentLoc = LM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    SWakeLock(SVC, true, coordinateCondTiming * 60000);
                    TimeOutHandler(SVC, true);
                    StartAutoUpdateService(TaskJOB);
                }
            }
        } catch (Exception e) {
            UIToast(SVC, "StartLocate\n" + e.getMessage());
        }
    }

    private class SLocationListener implements LocationListener {
        private final int coordinateCondDiff;

        private SLocationListener(int coordinateCondDiff) {
            this.coordinateCondDiff = coordinateCondDiff;
        }

        @Override
        public void onLocationChanged(Location Loc) {
            try {
                if (Loc != null) {
                    CurrentLoc = GetBestLocation(CurrentLoc, Loc);
                    double Distance = Loc.distanceTo(DistLoc);
                    if (!isArrived && Distance <= coordinateCondDiff) {
                        isArrived = true;//抵達目的地
                    } else if (isArrived && Distance >= coordinateCondDiff) {
                        isArrived = false;
                        PostLocData(CurrentLoc, CurrentLoc.distanceTo(DistLoc));//離開目標範圍時上傳最後一次
                        StopService(SVC);//離開之後 停止服務
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private Location GetBestLocation(Location CLoc, Location NewLoc) {
        if (NewLoc == null) return CLoc;
        if (CLoc.getTime() > NewLoc.getTime()) return CLoc;
        if (NewLoc.getAccuracy() <= CLoc.getAccuracy()) {
            return NewLoc;
        } else if (NewLoc.getTime() > CLoc.getTime() + (20 * 1000)) {
            return NewLoc;
        }
        return CLoc;
    }

    //Net===========================================================================================
    private long PreUpdateMillis;

    private void StartAutoUpdateService(JSONObject TaskJOB) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                int coordinateCondTime = SM.JSONIntGetter(TaskJOB, "coordinateCondTime", 10);// 每隔幾秒回傳資料
                while (SVC != null) {
                    try {
                        sleep(1000);
                        if (System.currentTimeMillis() - PreUpdateMillis >= coordinateCondTime * 1000) {
                            if (CurrentLoc != null && DistLoc != null) {
                                PostLocData(CurrentLoc, CurrentLoc.distanceTo(DistLoc));
                                PreUpdateMillis = System.currentTimeMillis();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void PostLocData(Location Loc, double Distance) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String PostUrl = SM.JSONStrGetter(TaskJOB, "coordinateCondReturnUrl");//Post 回報資訊URL
                    JSONObject ReturnJOB = new JSONObject();
                    ReturnJOB.put("input_data", TaskJOB);
                    ReturnJOB.put("provider", Loc.getProvider());
                    ReturnJOB.put("latitude", Loc.getLatitude());
                    ReturnJOB.put("longitude", Loc.getLongitude());
                    ReturnJOB.put("distance", Distance);
                    JSONObject ResultJOB = SM.SFetch(PostUrl, "", ReturnJOB);
                    Log.d("PostLocData", ResultJOB.toString());
                    ReturnJOB.remove("input_data");
                    //NLH.NetLog(SVC, "LocData", ReturnJOB.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //TimeOutHandler
    private static Thread TOThread;

    private static void TimeOutHandler(Context Con, boolean Start) {
        if (Start) {
            if (TOThread == null) {
                TOThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            long StartLocM = System.currentTimeMillis();
                            while (SVC != null && TaskJOB != null) {
                                sleep(500);
                                if (SVC != null && SVC.SM != null && TaskJOB != null) {
                                    int TimeOutMin = SVC.SM.JSONIntGetter(TaskJOB, "coordinateCondTiming", 240);
                                    if (System.currentTimeMillis() - StartLocM >= (TimeOutMin * 60000)) {
                                        StopService(Con);
                                        TOThread = null;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                TOThread.start();
            }
        } else {
            if (TOThread != null) {
                TOThread.interrupt();
                TOThread = null;
            }
        }
    }

    //Notification Run Foreground===================================================================
    private final static String NOTIFICATION_CHANNEL_ID = "GPSService";
    private final static String NOTIFICATION_CHANNEL_NAME = "GPSService";

    private void ShowNotification(String Title) {
        int SDKVersion = Build.VERSION.SDK_INT;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && SDKVersion >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon((SDKVersion >= Build.VERSION_CODES.LOLLIPOP && SDKVersion < Build.VERSION_CODES.N) ? R.drawable.ic_app : R.drawable.ic_app_white)
                .setSound(null)
                .setTicker(Title)
                .setVibrate(new long[]{})
                .setContentTitle(Title)
                .setContentText("定位服務執行中");

        startForeground(SLocService.ServiceNotifyID, notificationBuilder.build());// 开始前台服务
        /*
        if (isService) {
            startForeground(SLocService.ServiceNotifyID, notificationBuilder.build());// 开始前台服务
        } else {
            if (notificationManager != null) {
                notificationManager.notify(SLocService.ServiceNotifyID, notificationBuilder.build());
            }
        }

         */
    }

    //Toast=========================================================================================
    public static void UIToast(Context Con, int StringID) {
        try {
            UIToast(Con, Con.getResources().getString(StringID));
        } catch (Exception e) {
            //DebugToast("UIToast\n"+e.getMessage());
        }
    }

    public static void UIToast(Context Con, String ToastMsg) {
        try {
            if (TD.ShowDebugTos) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        Toast.makeText(Con, ToastMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //WakeLock======================================================================================
    private static PowerManager.WakeLock WL;

    private static void SWakeLock(Context Con, boolean Wake, long Timeout) {
        if (Wake) {
            PowerManager PM = (PowerManager) Con.getSystemService(Context.POWER_SERVICE);
            if (PM != null) {
                if (WL == null || !WL.isHeld()) {
                    WL = PM.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "JS:Loc");
                    WL.acquire(Timeout);//为了保证任务不被系统休眠打断，申请WakeLock
                }
            }
        } else {
            if (WL != null && WL.isHeld()) WL.release();
        }
    }
}
