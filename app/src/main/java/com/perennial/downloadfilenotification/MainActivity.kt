package com.perennial.downloadfilenotification

import android.Manifest
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.perennial.downloadfilenotification.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {

    private companion object {
        //PERMISSION request constant, assign any value
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
    }

    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var binding: ActivityMainBinding
    private lateinit var bitmap: Bitmap
    private lateinit var absolutePath: String
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notification: NotificationCompat.Builder
    private lateinit var pIntent: PendingIntent
    private lateinit var pendingIntent: PendingIntent

    private lateinit var taskEventReceiver: TaskEventReceiver
    private var isTaskCancelled: Boolean = false
    private var notificationID: Int = Random().nextInt()
    lateinit var downloadAysncTask: DownloadFileTask

    val channelID: String = "Download Notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        notificationManager = NotificationManagerCompat.from(this)
        taskEventReceiver = TaskEventReceiver()
        downloadAysncTask = DownloadFileTask()

        // adding action for broadcast
        val activityIntent = Intent(this, TaskEventReceiver()::class.java)
        activityIntent.putExtra("actionPerformed", "Action Performed")
        pIntent = PendingIntent.getBroadcast(this, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)


        val button = findViewById<Button>(R.id.notificationButton)
        binding.btnViewPDF.visibility = View.GONE
        binding.btnViewPDF.setOnClickListener {
            openPDF()
        }

        binding.btnViewPDF.text = "Cancel Task"

        binding.btnViewPDF.setOnClickListener {
            downloadAysncTask.cancel(true)
            notification.setContentTitle("Downloading cancelled..")
            notification.setContentText("File downloading cancelled..")
            notificationManager.notify(notificationID, notification.build())
        }

        if (checkPermission()) {
            toast("Permission already granted: create folder")
            createFolder()
        } else {
            toast("Please provide required permissions")
            requestPermission()
        }

        button.setOnClickListener {
            //https://www.shabakeh-mag.com/sites/default/files/files/attachment/1397/04/1530550032.pdf
            //https://kotlinlang.org/docs/kotlin-reference.pdf
            downloadAysncTask.execute("https://www.shabakeh-mag.com/sites/default/files/files/attachment/1397/04/1530550032.pdf")
            binding.btnViewPDF.visibility = View.VISIBLE

        }
    }

    inner class DownloadFileTask() : AsyncTask<String?, Int?, String?>() {

        override fun onPreExecute() {
            super.onPreExecute()

            notification = NotificationCompat.Builder(this@MainActivity, channelID)
                .setSmallIcon(R.drawable.ic_baseline_file_download_24)
                .setContentTitle("File Downloading...")
                .setContentText("Downloading in progress...")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_background, "CANCEL", pIntent)
            notificationManager.notify(notificationID, notification.build())

            /*  mProgressDialog.setMessage("Downloading file...")
              mProgressDialog.setIcon(R.drawable.ic_launcher_background)
              mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
              mProgressDialog.setCancelable(true)
              mProgressDialog.setCanceledOnTouchOutside(false)
              mProgressDialog.show()*/
        }

        override fun doInBackground(vararg strings: String?): String? {
            try{
                val imageUrl = URL(strings[0])
                val conn = imageUrl.openConnection() as HttpURLConnection
                conn.doInput = true
                conn.connect()
                val inputStream = conn.inputStream
                val fileLength = conn.contentLength

                val file = File("$absolutePath/kotlinR3.pdf")
                if (file.exists()) {
                    file.delete()
                }
                Log.d("Download fileLength", "$file")

                val data = ByteArray(1024)
                var count: Int
                var total: Long = 0

//                val options = BitmapFactory.Options()
//                options.inPreferredConfig = Bitmap.Config.RGB_565
//                bitmap = BitmapFactory.decodeStream(inputStream, null, options)!!

                val output = FileOutputStream(file)
                while (inputStream.read(data).also { count = it } != -1) {
                    if (isCancelled) {
                        return null
                    }
                    total += count.toLong()
                    if (fileLength > 0)
                        publishProgress((total * 100 / fileLength).toInt())
                    Log.d("Download progress", "${(total * 100 / fileLength).toInt()}")

                    output.write(data, 0, count)
                }
                output.flush()
                output.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onProgressUpdate(vararg progress: Int?) {
            super.onProgressUpdate(*progress)

                binding.notificationButton.text = "Downloading.. ${progress[0]}%"
                notification.setContentText("${progress[0]}%")
                notification.setProgress(100, progress[0]!!, false)
                notificationManager.notify(notificationID, notification.build())
        }

        override fun onCancelled(result: String?) {
            super.onCancelled(result)
            Log.d("onCancelled", result.toString())
        }

        override fun onPostExecute(result: String?) {
//            mProgressDialog.dismiss()
//            binding.ivDownloadPicture.setImageBitmap(result)
            Log.d("Download error", "$result")

            if (result != null) Toast.makeText(
                this@MainActivity,
                "Download error: $result",
                Toast.LENGTH_LONG
            ).show() else {
                Toast.makeText(this@MainActivity, "File downloaded", Toast.LENGTH_SHORT)
                    .show()
                binding.notificationButton.text = "Download completed"
                notification.setContentTitle("File Downloaded")
                notification.setContentText("Downloading completed.")
                notificationManager.notify(notificationID, notification.build())
                binding.btnViewPDF.text = "Open File"
            }

            if (isTaskCancelled) {
                notification.setContentTitle("File Downloaded")
                notification.setContentText("Downloading completed.")
                notificationManager.notify(notificationID, notification.build())
                binding.btnViewPDF.visibility = View.VISIBLE
                binding.btnViewPDF.text = "Download cancelled"

            }
        }
    }

    inner class TaskEventReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isTrue = intent?.getStringArrayExtra("actionPerformed")
            if (isTrue != null) {
                downloadAysncTask.cancel(true)
            }

        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "requestPermission: ", e)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            Environment.isExternalStorageManager()
        } else {
            //Android is below 11(R)
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    fun openPDF() {
        val pdfFile = File("$absolutePath/kotlinR2.pdf") // -> filename = maven.pdf
        Log.d("Download fileLength", "$pdfFile")

        val path = Uri.fromFile(pdfFile)
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(path.toString())
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this@MainActivity,
                "No Application available to view PDF",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "storageActivityResultLauncher: ")
            //here we will handle the result of our intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android is 11(R) or above
                if (Environment.isExternalStorageManager()) {
                    //Manage External Storage Permission is granted
                    Log.d(
                        TAG,
                        "storageActivityResultLauncher: Manage External Storage Permission is granted"
                    )
                    createFolder()
                } else {
                    //Manage External Storage Permission is denied....
                    Log.d(
                        TAG,
                        "storageActivityResultLauncher: Manage External Storage Permission is denied...."
                    )
                    toast("Manage External Storage Permission is denied....")
                }
            } else {
                //Android is below 11(R)
            }
        }


    private fun createFolder() {
        //folder name
        val folderName = "DownloadXT"
        //create folder using name we just input
        val file =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$folderName")
        if (file.exists()) {
            absolutePath = file.absolutePath
        } else {
            val folderCreated = file.mkdir()
            if (folderCreated) {
                absolutePath = file.absolutePath
                Log.d("Folder Created:", file.absolutePath)
                toast("Folder Created: ${file.absolutePath}")
            } else {
                toast("Folder not created....")
            }
        }
    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                //check each permission if granted or not
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read) {
                    //External Storage Permission granted
                    Log.d(TAG, "onRequestPermissionsResult: External Storage Permission granted")
                    createFolder()
                } else {
                    //External Storage Permission denied...
                    Log.d(TAG, "onRequestPermissionsResult: External Storage Permission denied...")
                    toast("External Storage Permission denied...")
                }
            }
        }
    }
}

/*
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
*/