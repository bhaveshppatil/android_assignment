package com.perennial.servicefiledownload

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission


internal object ConnectCheckerHelper {
    internal const val TIME_OUT_CONNECTION = 60 * 1000

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET])
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
