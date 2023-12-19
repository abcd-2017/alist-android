package com.android.alist.network

import android.content.Context
import com.android.alist.database.dao.ServiceDao
import com.android.alist.network.interceptor.RequestInterceptor
import com.android.alist.utils.constant.AppConstant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 网络请求工具
 */
class RetrofitClient(context: Context, val serviceDao: ServiceDao) {
    private var baseurl: String = ""
        get() {
            val defaultService = serviceDao.queryDefault(AppConstant.Default.TRUE.value)
            return "http://${defaultService.ip}/${defaultService.port}"
        }

    private val okHttpClient = OkHttpClient
        .Builder()
        //请求拦截器
        .addInterceptor(RequestInterceptor(context))
        .build();

    private fun getRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> getRequestApi(t: Class<T>): T {
        return getRetrofitClient().create(t)
    }
}