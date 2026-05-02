package com.android.alist.network.entity.fs

data class OfflineDownloadTaskEntity(
    val tasks: List<TaskItem>
)

data class TaskItem(
    val id: Int,
    val name: String,
    val state: Int,
    val status: String,
    val progress: Float,
    val error: String
)
