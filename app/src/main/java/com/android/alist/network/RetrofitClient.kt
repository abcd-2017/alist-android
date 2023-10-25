package com.android.alist.network

import android.content.Context
import com.android.alist.network.interceptor.RequireAuthorizationInterceptor
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
        .addInterceptor(RequireAuthorizationInterceptor(context))
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