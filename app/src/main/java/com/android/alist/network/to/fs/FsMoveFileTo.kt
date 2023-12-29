package com.android.alist.network.to.fs

data class FsMoveFileTo(
    val dst_dir: String,
    val names: List<String>,
    val src_dir: String
)