package com.perennial.database

import com.perennial.model.DownloaderData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
public interface DownloaderDao {

    @Query("SELECT * FROM download_data_table WHERE url IS :url")
    fun getDownloadByUrl(url: String): DownloaderData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewDownload(vararg item: DownloaderData)

    @Query("UPDATE download_data_table SET percent=:percent, size=:downloadedSize, totalSize=:totalSize WHERE url IS :url")
    fun updateDownload(url: String, percent: Int, downloadedSize: Int, totalSize: Int)
}