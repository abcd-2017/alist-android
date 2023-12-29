package com.android.alist.network.to.fs

data class FsBatchRenameTo(
    val rename_objects: List<RenameObject>,
    val src_dir: String
)

data class RenameObject(
    val new_name: String,
    val src_name: String
)