package com.mandalorian.chatapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mandalorian.chatapp.R
import com.mandalorian.chatapp.ui.MainActivity
import com.mandalorian.chatapp.utils.Constants

class MyService : Service() {
    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNotificationService()
        Toast.makeText(this, "Notification service stopped", Toast.LENGTH_LONG).show()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    fun startForegroundService() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_ID)
            .setContentTitle("Auto Reply BOT")
            .setContentText("Notification listener is running")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .build()

        startForeground(1, notification)
        startNotificationService()
    }

    fun startNotificationService() {
        Intent(this, NotificationService::class.java).also {
            it.action = "android.service.notification.NotificationListenerService"
            startService(it)
        }
    }

    fun stopNotificationService() {
        val intent = Intent(this, NotificationService::class.java).also {
            stopService(it)
        }
    }
}