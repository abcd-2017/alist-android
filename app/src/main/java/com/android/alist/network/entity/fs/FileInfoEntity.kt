package com.android.alist.network.entity.fs

data class FileInfoEntity(
    val is_dir: Boolean,
    val modified: String,
    val name: String,
    val provider: String,
    val raw_url: String,
    val readme: String,
    val related: Any?,
    val sign: String,
    val size: Long,
    val thumb: String,
    val type: Int,
    val created: String? = null,
    val hash_info: String? = null,
    val hashinfo: String? = null
)