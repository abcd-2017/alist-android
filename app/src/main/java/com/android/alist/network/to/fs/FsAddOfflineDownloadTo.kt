package com.android.alist.network.to.fs

data class FsAddOfflineDownloadTo(
    val urls: List<String>,
    val path: String,
    val tool: String,
    val delete_policy: String
)
