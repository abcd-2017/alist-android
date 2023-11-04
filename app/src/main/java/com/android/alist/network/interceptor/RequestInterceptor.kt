package com.android.alist.network.interceptor

import android.content.Context
import android.util.Log
import com.android.alist.network.annotation.RequireAuthorization
import com.android.alist.network.entity.ResponseData
import com.android.alist.utils.Constant
import com.android.alist.utils.HttpStatusCode
import com.android.alist.utils.SharePreferenceUtils
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http2.Http2
import retrofit2.http.HTTP

/**
 * 请求拦截器
 */
class RequestInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        //1.如果请求方法上带了这个注解，那么就请求头加上token这个参数
        val tag = request.tag(retrofit2.Invocation::class.java)
        //调用方法的信息
        val method = tag?.method()
        //是否带有这个标签
        val annotated = method?.annotations?.any { it is RequireAuthorization }
        var modifiedRequest = request
        if (annotated != null && annotated) {
            val token = SharePreferenceUtils.getData(Constant.TOKEN, "")

            if (token.isBlank()) {
                // TODO: 当token为空时，应该跳转到登陆界面
            }

            modifiedRequest = request.newBuilder()
                .addHeader("Authorization", token)
                .build()
        }

        //2.根据请求结果判断用户token是否过期
        val response = chain.proceed(modifiedRequest)
        Log.d("logs", "intercept: $response")

        val responseBody = response.body?.string()
        val responseData = Gson().fromJson(responseBody, ResponseData::class.java)
        //当请求结果为401,跳转到登陆界面
        if (responseData.code == HttpStatusCode.Unauthorized.code) {
            //TODO:当状态码为401，应该跳转到登陆界面
        }

        return response.newBuilder()
            .body(ResponseBody.create(response.body?.contentType(), responseBody ?: ""))
            .build()
    }
}