package com.perennial.servicefiledownload

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class NotificationApp : Application() {
    private val channelID = "Download Notification"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            val channel = NotificationChannel(
                channelID,
                "Download Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Download Notification Channel"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}