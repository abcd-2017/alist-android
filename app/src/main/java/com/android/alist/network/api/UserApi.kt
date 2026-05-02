package com.android.alist.network.api

import com.android.alist.network.annotation.RequireAuthorization
import com.android.alist.network.entity.ResponseData
import com.android.alist.network.entity.auth.FAKeyEntity
import com.android.alist.network.entity.auth.SshKeyEntity
import com.android.alist.network.entity.auth.SshKeyListEntity
import com.android.alist.network.entity.auth.UserInfoEntity
import com.android.alist.network.entity.auth.UserLoginEntity
import com.android.alist.network.to.auth.LoginUser
import com.android.alist.network.to.auth.SshKeyAddTo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * auth Api请求接口
 */
interface UserApi {
    /**
     * 获取某个用户的临时JWt token
     */
    @POST("/api/auth/login")
    suspend fun userLogin(
        @Body user: LoginUser
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

    /**
     * 获取SSH公钥列表
     */
    @GET("/api/me/sshkey/list")
    @RequireAuthorization
    suspend fun getSshKeyList(): ResponseData<SshKeyListEntity>

    /**
     * 添加SSH公钥
     */
    @POST("/api/me/sshkey/add")
    @RequireAuthorization
    suspend fun addSshKey(
        @Body sshKeyAddTo: SshKeyAddTo
    ): ResponseData<Unit>

    /**
     * 删除SSH公钥
     */
    @POST("/api/me/sshkey/delete")
    @RequireAuthorization
    suspend fun deleteSshKey(
        @Query("id") id: Int
    ): ResponseData<Unit>
}