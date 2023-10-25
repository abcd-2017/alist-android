package com.android.alist.network.entity.auth

data class UserInfoEntity(
    val Salt: String,
    val base_path: String,
    val disabled: Boolean,
    val id: Int,
    val otp: Boolean,
    val password: String,
    val permission: Int,
    val role: Int,
    val sso_id: String,
    val username: String
)