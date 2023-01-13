package com.perennial.downloadfilenotification

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notification: Builder
    private val IMAGE_URL =
        "https://cdn.pixabay.com/photo/2017/08/01/15/03/global-2566114_960_720.jpg"
    val channelID: String = "Download Notification"
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationManager = NotificationManagerCompat.from(this)
        imageView = findViewById(R.id.ivDownloadPicture)
        start()
    }

    private fun start() {
        DownloadTask().execute(IMAGE_URL)
    }

    inner class DownloadTask() : AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            notification = NotificationCompat.Builder(this@MainActivity, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Perennial System")
                .setContentText("File Downloading...")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setProgress(100, 0, false)
            notificationManager.notify(1, notification.build())
        }

        override fun doInBackground(vararg strings: String?): String? {
            try {

                var i = 10
                    while (i <= 100) {
                        try {
                            Thread.sleep(1000)
                            publishProgress(i)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        i += 10
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }

                /*  val url = URL(IMAGE_URL)
                val conexion: URLConnection = url.openConnection()
                conexion.connect()
                val lenghtOfFile: Int = conexion.contentLength
                val dir = applicationContext.cacheDir
                Log.d("PereLength", "Length of file: $lenghtOfFile")
                val input: InputStream = BufferedInputStream(url.openStream())
                val output: OutputStream =
                    FileOutputStream("download.jpg")

                Log.d("PereLength", "Length of file: /download.jpg")
                val data = ByteArray(1024)
                var total: Long = 0
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    publishProgress( (total * 100 / lenghtOfFile).toInt())
                    output.write(data, 0, count)
                }

                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                bitmapImage = BitmapFactory.decodeStream(
                    input, null,
                    options*/
            return ""
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            values[0]?.toInt()?.let { notification.setProgress(100, it, false) }
            notification.setContentText("${values[0]} %")
            notificationManager.notify(1, notification.build())
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            notification.setContentText("File Download Completed")
                .setProgress(0, 0, false)
                .setOngoing(false)

            notificationManager.notify(1, notification.build())
        }
    }
}
