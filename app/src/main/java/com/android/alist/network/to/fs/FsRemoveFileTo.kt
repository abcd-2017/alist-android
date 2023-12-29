package com.android.alist.network.to.fs

data class FsRemoveFileTo(
    val dir: String,
    val names: List<String>
)