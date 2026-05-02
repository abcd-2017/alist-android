package com.android.alist.network.entity.auth

data class SshKeyListEntity(
    val content: List<SshKeyEntity>,
    val total: Int
)
