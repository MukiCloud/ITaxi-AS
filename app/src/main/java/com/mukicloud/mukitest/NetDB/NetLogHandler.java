package com.mukicloud.mukitest.NetDB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NetLogHandler {
    private static NetLogDBHlp NDB;
    private ArrayList<NetLogSite> DataAL = new ArrayList<>();

    public NetLogHandler() {
        NDB = new NetLogDBHlp();
    }

    public void NetLog(Context Con, String Action, String Value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault());
        String Time = sdf.format(new Date());
        @SuppressLint("HardwareIds")
        String UserID = Settings.Secure.getString(Con.getContentResolver(), Settings.Secure.ANDROID_ID);
        DataAL.add(new NetLogSite("", Time, UserID, Action, Value));
        StartNetLog();
    }

    private static boolean ThreadRun = false;

    private void StartNetLog() {
        if (!ThreadRun) {
            ThreadRun = true;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        if (NDB.ConnectSQL()) {
                            while (DataAL.size() > 0) {
                                if (NDB.InsertDB(DataAL.get(0))) DataAL.remove(0);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("NetLog", "" + e.getMessage());
                    } finally {
                        ThreadRun = false;
                    }
                }
            }.start();
        }
    }
}
