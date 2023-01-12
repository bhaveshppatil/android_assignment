package com.perennial.workmanagersample.workManager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.perennial.workmanagersample.constant.Constants.IMAGE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import okio.buffer
import okio.sink
import java.io.File

class DownloadWorkManager(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        val networkClient = OkHttpClient()
        val networkRequest = Request.Builder()
            .url(IMAGE_URL)
            .build()

        val dir = applicationContext.cacheDir
        val downloadImage = File(dir, "map.jpg")
        try {
            if (downloadImage.exists()) {
                downloadImage.delete()
            }
            networkClient.newCall(networkRequest).execute().use { response ->
                val link = downloadImage.sink().buffer()
                response.body?.let {
                    link.writeAll(it.source())
                }
                link.close()
            }
        } catch (e: IOException) {
            Log.d("Perennial", e.message.toString())
            return Result.failure()
        }
        return Result.success()

    }
}