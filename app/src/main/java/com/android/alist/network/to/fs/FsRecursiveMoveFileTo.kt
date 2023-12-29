package com.android.alist.network.to.fs

data class FsRecursiveMoveFileTo(
    val dst_dir: String,
    val src_dir: String
)