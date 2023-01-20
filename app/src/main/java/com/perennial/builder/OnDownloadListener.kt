package com.perennial.builder

import java.io.File


interface OnDownloadListener {
    fun onStart()
    fun onPause()
    fun onResume()
    fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int)
    fun onCompleted(file: String?)
    fun onFailure(reason: String?)
    fun onCancel()
}
