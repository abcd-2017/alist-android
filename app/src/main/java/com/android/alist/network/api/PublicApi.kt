package com.android.alist.network.api

import com.android.alist.network.entity.ResponseData
import com.android.alist.network.entity.pub.PublicSettingsEntity
import retrofit2.http.GET

interface PublicApi {
    /**
     * 连通性ping检测
     */
    @GET("/ping")
    suspend fun ping(): ResponseData<Unit>

    /**
     * 获取站点配置
     */
    @GET("/api/public/settings")
    suspend fun getPublicSettings(): ResponseData<PublicSettingsEntity>
}
