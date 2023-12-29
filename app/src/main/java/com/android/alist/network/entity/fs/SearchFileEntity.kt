package com.android.alist.network.entity.fs

data class SearchFileEntity(
    val content: List<FileContent>,
    val total: Int
)

data class FileContent(
    val is_dir: Boolean,
    val name: String,
    val parent: String,
    val size: Int,
    val type: Int
)