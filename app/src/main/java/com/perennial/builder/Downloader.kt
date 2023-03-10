package com.perennial.builder

import android.Manifest
import android.app.NotificationChannel
import android.content.Context
import android.os.AsyncTask
import androidx.annotation.RequiresPermission
import com.perennial.database.DownloaderDao
import com.perennial.servicefiledownload.ForegroundService
import java.lang.ref.WeakReference

class Downloader private constructor(downloadTask: ForegroundService.DownloadFileTask) : IDownload {

    private var mDownloadTask: ForegroundService.DownloadFileTask? = null

    init {
        if (mDownloadTask == null)
            mDownloadTask = downloadTask
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun download() {
        if (mDownloadTask == null)
            throw IllegalAccessException("Rebuild new instance after \"pause or cancel\" download")
        mDownloadTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun cancelDownload() {
        mDownloadTask?.cancel()
        mDownloadTask = null
    }

    override fun pauseDownload() {
        mDownloadTask?.pause()
        mDownloadTask = null
    }

    override fun resumeDownload() {
//        mDownloadTask?.resume = true
//        download()
    }

    class Builder(
        private val context: Context,
        private val dao: DownloaderDao,
        private val absolutePath: String,
        private val fileURl: String,
        private val notificationChannel: NotificationChannel
    ) {
        private var downloadListener: OnDownloadListener? = null

        fun downloadListener(downloadListener: OnDownloadListener): Builder {
            this.downloadListener = downloadListener
            return this
        }

        fun build(): Downloader {
            val downloadTask = ForegroundService(
                context = context,
                dao = dao,
                absolutePath = absolutePath,
                downloadListener = downloadListener!!,
                fileURl = fileURl,
                notificationChannel = notificationChannel
            ).DownloadFileTask()
            return Downloader(downloadTask)
        }
    }
}
