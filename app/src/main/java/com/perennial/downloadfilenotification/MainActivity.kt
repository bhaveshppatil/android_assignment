package com.perennial.asynctasknotification

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.perennial.downloadfilenotification.R
import com.perennial.downloadfilenotification.databinding.ActivityMainBinding
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mProgressDialog = ProgressDialog(this)
        val button = findViewById<Button>(R.id.notificationButton)

        button.setOnClickListener {
            MyAsyncTask(this).execute("https://shanniz.github.io/courses/mobileappdev/MAD_AsyncTask_Notifications.pdf")
        }
    }

    inner class MyAsyncTask(context: Context) : AsyncTask<String?, Int?, String?>() {
        private val context: Context

        init {
            this.context = context
        }

        override fun onPreExecute() {
            super.onPreExecute()
            mProgressDialog.setMessage("Downloading file...")
            mProgressDialog.setIcon(R.drawable.ic_launcher_background)
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            mProgressDialog.show()
        }

        override fun doInBackground(vararg sUrl: String?): String? {
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name)
            wl.acquire()
            try {
                var input: InputStream? = null
                var output: OutputStream? = null
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(sUrl[0])
                    connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection!!.responseCode != HttpURLConnection.HTTP_OK) return ("Server returned HTTP " + connection.responseCode
                            + " " + connection.responseMessage)
                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    val fileLength = connection.contentLength
                    // download the file
                    input = connection.inputStream
                    output = FileOutputStream(Environment.getExternalStorageDirectory().absolutePath +"/download.pdf")
                    Log.d("Filepath", output.toString())
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        // allow canceling with back button
                        if (isCancelled) return null
                        total += count.toLong()
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((total * 100 / fileLength).toInt())
                        output.write(data, 0, count)
                    }
                } catch (e: Exception) {
                    return e.toString()
                } finally {
                    try {
                        output?.close()
                        input?.close()
                    } catch (ignored: IOException) {
                    }
                    connection?.disconnect()
                }
            } finally {
                wl.release()
            }
            return null
        }

        override fun onProgressUpdate(vararg progress: Int?) {
            super.onProgressUpdate(*progress)
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false)
            mProgressDialog.setMax(100)
            mProgressDialog.setProgress(progress[0]!!)
        }

        override fun onPostExecute(result: String?) {
            mProgressDialog.dismiss()
            if (result != null) Toast.makeText(
                context,
                "Download error: $result",
                Toast.LENGTH_LONG
            ).show() else Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show()
        }
    }
}