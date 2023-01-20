package com.perennial.servicefiledownload

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.perennial.broadcast.EventReceiver
import com.perennial.builder.OnDownloadListener
import com.perennial.database.DownloaderDao
import com.perennial.model.DownloaderData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ForegroundService(
    val context: Context,
    val dao: DownloaderDao,
    val absolutePath: String,
    val downloadListener: OnDownloadListener,
    var fileURl: String,
    val notificationChannel: NotificationChannel
) : Service() {

    companion object {
        lateinit var downloadTask: DownloadFileTask
        lateinit var notification: Notification.Builder
        var notificationID: Int = Random().nextInt()
        val channelID: String = "Download Notification"
    }

    private lateinit var eventReceiver: EventReceiver
    private lateinit var pendingIntent: PendingIntent
    private var isTaskPaused: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        eventReceiver = EventReceiver()
        PrefsHelper.init(this)

        val intent = Intent(this, EventReceiver()::class.java)
        intent.putExtra("isTaskPaused", true)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )

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
            downloadListener.onStart()
            isTaskPaused = PrefsHelper.read(PrefsHelper.IS_DOWNLOAD_PAUSED, false)

            if (!isTaskPaused) {
                dao.insertNewDownload(
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
//            downloadListener.onResume()
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
                    val model = dao.getDownloadByUrl(fileURl)
                    percent = model.percent
                    downloadedSize = model.size
                    totalSize = model.totalSize
                    connection.allowUserInteraction = true
                    connection.setRequestProperty("Range", "bytes=" + model.size + "-")
                    PrefsHelper.write(PrefsHelper.IS_DOWNLOAD_PAUSED, false)
                }
                connection.connect()

                if (!isTaskPaused) totalSize = connection.contentLength

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
                        downloadListener.onProgressUpdate(percent, downloadedSize, totalSize)
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
                connection.disconnect()
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

        @RequiresApi(Build.VERSION_CODES.R)
        override fun onCancelled(result: String?) {
            super.onCancelled(result)
            notification.setAutoCancel(true)
            notification.setContentTitle("Downloading cancelled")
            notification.setContentText("File download cancelled")
            notification.setOngoing(false)
            notification.setProgress(0, 0, false)
            notification.setFlag(Notification.FLAG_AUTO_CANCEL, true)
            notification.setDeleteIntent(pendingIntent)
            startForeground(notificationID, notification.build())
            Log.d("onCancelled111", result.toString())
        }

        override fun onPostExecute(result: String?) {
            Log.d("Download error", "$result")
            if (result != null)
                downloadListener.onFailure(result)
             else if (isCancelled) {
                notification.setContentText("Downloading cancelled")
                notification.setContentText("File download cancelled")
                notification.setOngoing(false)
                startForeground(notificationID, notification.build())
            } else
                downloadListener.onCompleted(absolutePath)
                Toast.makeText(
                    this@ForegroundService,
                    "File downloaded\n$absolutePath",
                    Toast.LENGTH_LONG
                )
                    .show()
            notification.setContentTitle("File Downloaded")
            notification.setContentText("Downloading completed.")
            notification.setAutoCancel(false)
            startForeground(notificationID, notification.build())
            stopForeground(notificationID)
        }

        internal fun cancel() {
            downloadListener.onCancel()
            cancel(true)
            dao.updateDownload(fileURl, percent, downloadedSize, totalSize)
        }

        internal fun pause() {
            cancel(true)
            dao.updateDownload(fileURl, percent, downloadedSize, totalSize)
            downloadListener.onPause()
        }
    }
}


