package com.android.alist.network.entity.fs

class DirsEntity : ArrayList<DirsEntityItem>()

data class DirsEntityItem(
    val modified: String,
    val name: String
)