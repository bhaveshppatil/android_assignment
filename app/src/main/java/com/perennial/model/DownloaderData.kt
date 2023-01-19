package com.perennial.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "download_data_table")
data class DownloaderData(
    @PrimaryKey val id: Int,
    val url: String?,
    val filename: String?,
    val percent: Int,
    val size: Int,
    val totalSize: Int
)