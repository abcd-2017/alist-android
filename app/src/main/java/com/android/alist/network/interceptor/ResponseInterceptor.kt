package com.android.alist.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val responseBody = response.body()?.string()

        return response.newBuilder()
            .body(ResponseBody.create(response.body()?.contentType(), responseBody ?: ""))
            .build()
    }
}