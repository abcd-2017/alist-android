package com.android.alist.network.entity.fs

data class FileListEntity(
    val content: List<File>,
    val provider: String,
    val readme: String,
    val total: Int,
    val write: Boolean
)

data class File(
    val is_dir: Boolean,
    val modified: String,
    val name: String,
    val sign: String,
    val size: Int,
    val thumb: String,
    val type: Int
)