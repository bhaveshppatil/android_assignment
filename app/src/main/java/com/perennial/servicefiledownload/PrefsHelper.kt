package com.perennial.servicefiledownload

import android.content.Context
import android.content.SharedPreferences

object PrefsHelper {

    private lateinit var prefs: SharedPreferences

    private const val PREFS_NAME = "DownloadStatePrefs"
    const val IS_DOWNLOAD_PAUSED = "paused"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    fun write(key: String, value: Boolean) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putBoolean(key, value)
            commit()
        }
    }

    fun read(key: String, value: Boolean): Boolean {
        return prefs.getBoolean(key, value)
    }
}
