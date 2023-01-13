package com.perennial.receivernotifier.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.perennial.receivernotifier.R

class EventReceiver : BroadcastReceiver() {
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notification: NotificationCompat.Builder
    val channelID: String = "Download Notification"

    override fun onReceive(context: Context, intent: Intent?) {
        val isAirplaneModeEnabled = intent?.getBooleanExtra("state", false) ?: return
        notificationManager = NotificationManagerCompat.from(context)

        if(Settings.System.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)== 0){
            Toast.makeText(context, "AirplaneMode Enabled", Toast.LENGTH_SHORT).show()
            createNotification(context = context!!, "AirplaneMode Enabled", "You have enabled Airplane mode")
        }else{
            Toast.makeText(context, "AirplaneMode Enabled", Toast.LENGTH_SHORT).show()
            createNotification(context = context!!, "AirplaneMode Disabled", "You have disabled Airplane mode")

        }

        when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
            WifiManager.WIFI_STATE_UNKNOWN)) {
            WifiManager.WIFI_STATE_ENABLED -> {
                Toast.makeText(context, "WIFI is On", Toast.LENGTH_SHORT).show()
                createNotification(context = context!!, "WIFI Eisabled", "You have enabled wifi mode")
            }
            WifiManager.WIFI_STATE_DISABLED -> {
                Toast.makeText(context, "WIFI Disabled", Toast.LENGTH_SHORT).show()
                createNotification(context = context!!, "WIFI Disabled", "You have disabled WIFI mode")

            }
        }
    }

    private fun createNotification(context: Context, message: String, subContent : String){

        notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message)
            .setContentText(subContent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
        notificationManager.notify(1, notification.build())
    }
}