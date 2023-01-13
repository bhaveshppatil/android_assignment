package com.perennial.downloadfilenotification

import android.content.Context
import com.perennial.downloadfilenotification.app.DownloadTask
 class ImageDownloadTask(
    val requireContext: Context,
    val s: String,
    val fileName: String,
    val id: Int,
    val listener: Any
) {

}