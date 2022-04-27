package com.mukicloud.mukitest.SFunc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.mukicloud.mukitest.R

class SForegroundService: LifecycleService() {
    private val svc = this
    var sm: SMethods? = null
    override fun onCreate() {
        super.onCreate()
        sm = SMethods(svc)
        showServiceNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY //殺不死方法之一  //return super.onStartCommand(intent, flags, startId);
    }

    override fun onDestroy() {
//        startService(this)
        stopForeground(true) // 停止前台服务
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun startService(Con: Context) {
            val intent = Intent(Con, SForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Con.startForegroundService(intent)
            } else {
                Con.startService(intent)
            }
        }
    }

    //Notification Run Foreground===================================================================
    private val NOTIFICATION_CHANNEL_ID = "NotificationService"
    private val NOTIFICATION_CHANNEL_NAME = "NotificationService"
    private fun showServiceNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(false)
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val sdkVersion = Build.VERSION.SDK_INT
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(if (sdkVersion >= Build.VERSION_CODES.LOLLIPOP && sdkVersion < Build.VERSION_CODES.N) R.drawable.ic_app else R.drawable.ic_app)
            .setSound(null)
            .setVibrate(null)
            .setTicker(sm!!.IDStr(R.string.app_name))
            .setContentTitle(sm!!.IDStr(R.string.app_name))
            .setContentText(sm!!.IDStr(R.string.app_name) + " NotificationService")

        val notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_ONGOING_EVENT
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
        notification.flags = notification.flags or Notification.FLAG_FOREGROUND_SERVICE
        startForeground(102, notification) // 开始前台服务
    }
}