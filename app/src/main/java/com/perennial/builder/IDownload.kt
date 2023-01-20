package com.perennial.builder


internal interface IDownload {
    fun download()
    fun cancelDownload()
    fun pauseDownload()
    fun resumeDownload()
}