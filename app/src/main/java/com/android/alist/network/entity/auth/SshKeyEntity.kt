package com.android.alist.network.entity.auth

data class SshKeyEntity(
    val id: Int,
    val title: String,
    val fingerprint: String,
    val added_time: String,
    val last_used_time: String
)
