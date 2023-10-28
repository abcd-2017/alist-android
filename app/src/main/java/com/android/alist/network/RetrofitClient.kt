package com.android.alist.network

import android.content.Context
import com.android.alist.network.interceptor.RequireAuthorizationInterceptor
import com.android.alist.network.interceptor.ResponseInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 网络请求工具
 */
class RetrofitClient(context: Context) {
    private var BASE_URL: String = ""
        get() {
            return "http://localhost:80"
        }

    private val okHttpClient = OkHttpClient
        .Builder()
        //校验权限注解拦截器
        .addInterceptor(RequireAuthorizationInterceptor(context))
        //请求响应拦截器
        .addInterceptor(ResponseInterceptor())
        .build();

    private fun getRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> getRequestApi(t: Class<T>): T {
        return getRetrofitClient().create(t)
    }
}