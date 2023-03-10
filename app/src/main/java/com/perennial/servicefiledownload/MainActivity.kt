package com.perennial.servicefiledownload

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.perennial.builder.Downloader
import com.perennial.builder.OnDownloadListener
import com.perennial.database.DownloaderDao
import com.perennial.database.DownloaderDatabase
import com.perennial.servicefiledownload.databinding.ActivityMainBinding
import java.io.File

/***
 * Still some corrections are pending.
***/

class MainActivity : AppCompatActivity() {

    private companion object {
        //PERMISSION request constant, assign any value
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
        private const val FILE_URL =
            "https://www.ebookfrenzy.com/pdf_previews/Kotlin30EssentialsPreview.pdf"
    }

    private lateinit var absolutePath: String
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var dao: DownloaderDao
    private var downloader: Downloader? = null
    private lateinit var binding: ActivityMainBinding
    private val handler: Handler = Handler()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        notificationChannel = NotificationChannel(
            ForegroundService.channelID,
            "Foreground service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        dao = DownloaderDatabase.getAppDatabase(this).downloaderDao()

        if (checkPermission()) {
            toast("Permission already granted: create folder")
            createFolder()
        } else {
            toast("Please provide required permissions")
            requestPermission()
        }
        if (createFolder()) {
            getDownloadUpdates()
            if (!isForegroundServiceRunning()) {
                val intent = Intent(this, ForegroundService::class.java)

            }
        }
        binding.startDownloadBtn.setOnClickListener {
            getDownloadUpdates()
            downloader?.download()
        }
        binding.cancelDownloadBtn.setOnClickListener {
            downloader?.cancelDownload()
        }
        binding.pauseDownloadBtn.setOnClickListener {
            downloader?.pauseDownload()
        }
        binding.resumeDownloadBtn.setOnClickListener {
            getDownloadUpdates()
            downloader?.resumeDownload()
        }
    }

    private fun getDownloadUpdates() {
        downloader = Downloader.Builder(
            this,
            dao,
            absolutePath,
            FILE_URL,
            notificationChannel,
        ).downloadListener(object : OnDownloadListener {

            override fun onStart() {
                handler.post { binding.currentStatusTxt.text = "onStart" }
                Log.d(TAG, "onStart")
            }

            override fun onPause() {
                handler.post { binding.currentStatusTxt.text = "onPause" }
                Log.d(TAG, "onPause")
            }

            override fun onResume() {
                handler.post { binding.currentStatusTxt.text = "onResume" }
                Log.d(TAG, "onResume")
            }

            override fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int) {
                handler.post {
                    binding.currentStatusTxt.text = "onProgressUpdate"
                    binding.percentTxt.text = percent.toString().plus("%")
                    binding.sizeTxt.text = getSize(downloadedSize)
                    binding.totalSizeTxt.text = getSize(totalSize)
                    binding.downloadProgress.progress = percent
                }
                Log.d(
                    TAG,
                    "onProgressUpdate: percent --> $percent downloadedSize --> $downloadedSize totalSize --> $totalSize "
                )
            }

            override fun onCompleted(file: String?) {
                handler.post { binding.currentStatusTxt.text = "onCompleted" }
                Log.d(TAG, "onCompleted: file --> $file")
            }

            override fun onFailure(reason: String?) {
                handler.post { binding.currentStatusTxt.text = "onFailure: reason --> $reason" }
                Log.d(TAG, "onFailure: reason --> $reason")
            }

            override fun onCancel() {
                handler.post { binding.currentStatusTxt.text = "onCancel" }
                Log.d(TAG, "onCancel")
            }

        }).build()
    }

    private fun isForegroundServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (ForegroundService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
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


    private fun createFolder(): Boolean {
        //folder name
        val folderName = "DownloadXT"
        //create folder using name we just input
        val file =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$folderName")
        if (file.exists()) {
            absolutePath = file.absolutePath
            return true
        } else {
            val folderCreated = file.mkdir()
            return if (folderCreated) {
                absolutePath = file.absolutePath
                Log.d("Folder Created:", file.absolutePath)
                toast("Folder Created: ${file.absolutePath}")
                true
            } else {
                toast("Folder not created....")
                false
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

    fun getSize(size: Int): String {
        var s = ""
        val kb = (size / 1024).toDouble()
        val mb = kb / 1024
        val gb = kb / 1024
        val tb = kb / 1024
        if (size < 1024) {
            s = "$size Bytes"
        } else if (size >= 1024 && size < 1024 * 1024) {
            s = String.format("%.2f", kb) + " KB"
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
            s = String.format("%.2f", mb) + " MB"
        } else if (size >= 1024 * 1024 * 1024 && size < 1024 * 1024 * 1024 * 1024) {
            s = String.format("%.2f", gb) + " GB"
        } else if (size >= 1024 * 1024 * 1024 * 1024) {
            s = String.format("%.2f", tb) + " TB"
        }
        return s
    }
}