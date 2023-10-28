package com.android.alist.network.api

import com.android.alist.network.annotation.RequireAuthorization
import com.android.alist.network.entity.ResponseData
import com.android.alist.network.entity.auth.FAKeyEntity
import com.android.alist.network.entity.auth.UserInfoEntity
import com.android.alist.network.entity.auth.UserLoginEntity
import retrofit2.http.GET
import retrofit2.http.POST


/**
 * auth Api请求接口
 */
interface UserApi {
    /**
     * 获取某个用户的临时JWt token
     */
    @POST("/api/auth/login")
    suspend fun userLogin(
        username: String,
        password: String
    ): ResponseData<UserLoginEntity>

    @POST("/api/auth/login/hash")
    suspend fun hashLogin(
        username: String,
        password: String
    ): ResponseData<UserLoginEntity>

    /**
     * 生成2FA密钥
     */
    @POST("/api/auth/2fa/generate")
    suspend fun generate(): ResponseData<FAKeyEntity>

    /**
     * 验证2FA code
     */
    @POST("/api/auth/2fa/verify")
    suspend fun verify2fa(
        code: String,
        secret: String
    ): ResponseData<Any>

    /**
     * 获取用户信息
     */
    @GET("/api/me")
    @RequireAuthorization
    suspend fun getUserInfo(): ResponseData<UserInfoEntity>
}