package com.perennial.servicefiledownload

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.util.Pair
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.perennial.broadcast.EventReceiver
import com.perennial.database.DownloaderDao
import com.perennial.database.DownloaderDatabase
import com.perennial.model.DownloaderData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ForegroundService() : Service() {
    private val fileURl =
        "https://www.shabakeh-mag.com/sites/default/files/files/attachment/1397/04/1530550032.pdf"
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notification: Notification.Builder
    lateinit var downloadTask: DownloadFileTask
    private lateinit var absolutePath: String
    private var notificationID: Int = Random().nextInt()
    private lateinit var eventReceiver: EventReceiver
    private lateinit var pendingIntent: PendingIntent
    private lateinit var dao: DownloaderDao
    private var isTaskPaused: Boolean = false

    val channelID: String = "Download Notification"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        absolutePath = intent?.getStringExtra("absolutePath").toString()

        eventReceiver = EventReceiver()
        PrefsHelper.init(this)
        val intent = Intent(this, EventReceiver()::class.java)
        intent.putExtra("isTaskPaused", true)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        notificationChannel = NotificationChannel(
            channelID,
            "Foreground service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )
        dao = DownloaderDatabase.getAppDatabase(this).downloaderDao()
        downloadTask = DownloadFileTask()
        downloadTask.execute(fileURl)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    inner class DownloadFileTask() : AsyncTask<String?, Int?, String?>() {
        private lateinit var downloadedFile: File
        private var downloadedSize: Int = 0
        private var percent: Int = 0
        private var totalSize: Int = 0

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPreExecute() {
            super.onPreExecute()
            isTaskPaused = PrefsHelper.read(PrefsHelper.IS_DOWNLOAD_PAUSED, false)

            if (!isTaskPaused) {
                dao?.insertNewDownload(
                    DownloaderData(
                        0,
                        fileURl,
                        absolutePath,
                        0,
                        0,
                        0
                    )
                )
            }
            notification = Notification.Builder(this@ForegroundService, channelID)
                .setContentText("File downloading")
                .setContentTitle("Service enabled, Downloading in progress..")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_background, "True", pendingIntent)
            startForeground(notificationID, notification.build())
        }

        override fun doInBackground(vararg strings: String?): String? {
            try {
                val imageUrl = URL(strings[0])
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.doInput = true

                if (isTaskPaused) {
                    PrefsHelper.write(PrefsHelper.IS_DOWNLOAD_PAUSED, false)
                    val model = dao?.getDownloadByUrl(fileURl)
                    percent = model?.percent!!
                    downloadedSize = model.size
                    totalSize = model.totalSize
                    connection?.allowUserInteraction = true
                    connection?.setRequestProperty("Range", "bytes="+model.size + "-")
                    PrefsHelper.write(PrefsHelper.IS_DOWNLOAD_PAUSED, false)
                }
                connection.connect()

                if (!isTaskPaused) totalSize = connection?.contentLength!!

                downloadedFile = File("$absolutePath/kotlinR3.pdf")
                if (downloadedFile.exists()) {
                    downloadedFile.delete()
                }
                Log.d("Download fileLength", "$downloadedFile")
                val buffer = ByteArray(32 * 1024)
                var count: Int
                var total: Long = 0
                var previousPercent = -1

                val bufferedInputStream = connection.inputStream
                val fileLength = connection.contentLength
                val bufferedOutputStream = FileOutputStream(downloadedFile)

                // update percent, size file downloaded
                while (bufferedInputStream.read(buffer, 0, 1024)
                        .also { count = it } >= 0 && !isCancelled
                ) {
                    bufferedOutputStream.write(buffer, 0, count)
                    downloadedSize = downloadedSize.plus(count)
                    percent = (100.0f * downloadedSize.toFloat() / totalSize.toLong()).toInt()
                    if (previousPercent != percent) {
                        publishProgress(percent, downloadedSize, totalSize)
                        previousPercent = percent
                        dao?.updateDownload(
                            fileURl,
                            percent,
                            downloadedSize,
                            totalSize
                        )
                    }
                }

             /*   while (inputStream.read(data).also { count = it } != -1) {
                    if (isCancelled) {
                        return null
                    }
                    total += count.toLong()
                    if (fileLength > 0)
                        publishProgress((total * 100 / fileLength).toInt())
                    Log.d("Download progress", "${(total * 100 / fileLength).toInt()}")

                    output.write(data, 0, count)
                }*/
                bufferedOutputStream.flush()
                bufferedOutputStream.close()
                bufferedInputStream.close()
                connection?.disconnect()
             /*   output.flush()
                output.close()
                inputStream.close()*/
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onProgressUpdate(vararg progress: Int?) {
            super.onProgressUpdate(*progress)
            notification.setContentText("${progress[0]}%")
            notification.setProgress(100, progress[0]!!, false)
            startForeground(notificationID, notification.build())
        }

        override fun onCancelled(result: String?) {
            super.onCancelled(result)
            Log.d("onCancelled", result.toString())
        }

        override fun onPostExecute(result: String?) {
            Log.d("Download error", "$result")

            if (result != null) Toast.makeText(
                this@ForegroundService,
                "Download error: $result",
                Toast.LENGTH_LONG
            ).show() else {
                Toast.makeText(this@ForegroundService, "File downloaded\n$absolutePath", Toast.LENGTH_LONG)
                    .show()
                notification.setContentTitle("File Downloaded")
                notification.setContentText("Downloading completed.")
                notification.setAutoCancel(true)
                startForeground(notificationID, notification.build())
                stopForeground(notificationID)
            }
        }
    }
}

