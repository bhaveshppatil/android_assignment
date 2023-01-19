package com.perennial.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.perennial.servicefiledownload.ForegroundService
import com.perennial.servicefiledownload.PrefsHelper

class EventReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            PrefsHelper.init(context)
        }
        val isTaskPaused = intent?.getBooleanExtra("isTaskPaused", false)
        if (isTaskPaused == true) {
            PrefsHelper.write(PrefsHelper.IS_DOWNLOAD_PAUSED, true)
        }

        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val service = Intent(context, ForegroundService::class.java)
            context?.startForegroundService(service)
        }

        Log.d("isTaskPaused", isTaskPaused.toString())
    }
}