package com.perennial.`interface`

interface PerformDownloadActions {
    fun onTaskExecute()
    fun onTaskProgressUpdate(max: Int, progress: Int, intermediate: Boolean)
    fun onTaskCancelled(isCancelled: Boolean)
    fun onPostExecute(result: String?)
}