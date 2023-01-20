package com.perennial.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.perennial.servicefiledownload.ForegroundService
import com.perennial.servicefiledownload.ForegroundService.Companion.channelID
import com.perennial.servicefiledownload.PrefsHelper
import com.perennial.servicefiledownload.R
import java.util.*

class EventReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notification: NotificationCompat.Builder
    private var notificationID: Int = Random().nextInt()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null) {
            PrefsHelper.init(context)
            notificationManager = NotificationManagerCompat.from(context)
            val isTaskPaused = intent?.getBooleanExtra("isTaskPaused", false)
            if (isTaskPaused == true) {
                PrefsHelper.write(PrefsHelper.IS_DOWNLOAD_PAUSED, true)
                ForegroundService.downloadTask.cancel(true)
            }
            Log.d("isTaskPaused", isTaskPaused.toString())
        }

        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val service = Intent(context, ForegroundService::class.java)
            context?.startForegroundService(service)
        }
    }

    fun createCancelledNotification(context: Context) {
        notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_baseline_warning_24)
            .setContentTitle("File Downloading...")
            .setContentText("Downloading in progress...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
        notificationManager.notify(notificationID, notification.build())
    }
}