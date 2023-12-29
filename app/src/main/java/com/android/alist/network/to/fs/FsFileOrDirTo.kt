package com.android.alist.network.to.fs

data class FsFileOrDirTo(
    val page: Int,
    val password: String,
    val path: String,
    val per_page: Int,
    val refresh: Boolean
)