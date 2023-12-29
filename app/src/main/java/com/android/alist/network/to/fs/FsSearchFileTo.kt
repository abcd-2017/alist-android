package com.android.alist.network.to.fs

data class FsSearchFileTo(
    val keywords: String,
    val page: Int,
    val parent: String,
    val password: String,
    val per_page: Int,
    val scope: Int
)