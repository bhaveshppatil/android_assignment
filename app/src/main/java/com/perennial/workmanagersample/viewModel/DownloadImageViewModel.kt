package com.perennial.workmanagersample.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.work.*
import com.perennial.workmanagersample.workManager.DownloadWorkManager

class DownloadImageViewModel(application: Application) : AndroidViewModel(application) {
    val isImageDownloadingFinished = MediatorLiveData<WorkInfo>()

    fun performImageDownloadWork() {
        val downloadImage = OneTimeWorkRequest.Builder(DownloadWorkManager::class.java)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresCharging(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .addTag("downloadImage")
            .build()
        WorkManager.getInstance(getApplication())
            .beginWith(downloadImage)
            .enqueue()
        downloadImageStatus(downloadImage)

    }

    private fun downloadImageStatus(oneTimeWorkRequest: OneTimeWorkRequest) {
        val downloadImageStatus = WorkManager.getInstance(getApplication())
            .getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
        isImageDownloadingFinished.addSource(downloadImageStatus) { workStatus ->
            isImageDownloadingFinished.value = workStatus

            if (workStatus.state.isFinished) {
                isImageDownloadingFinished.removeSource(downloadImageStatus)
            }
        }
    }
}