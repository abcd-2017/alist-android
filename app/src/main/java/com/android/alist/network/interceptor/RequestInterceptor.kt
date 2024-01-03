package com.android.alist.network.interceptor

import android.util.Log
import com.android.alist.App
import com.android.alist.network.annotation.RequireAuthorization
import com.android.alist.network.entity.ResponseData
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.constant.HttpStatusCode
import com.android.alist.utils.SharePreferenceUtils
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * 请求拦截器
 */
class RequestInterceptor() : Interceptor {
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
            val token = SharePreferenceUtils.getData(AppConstant.TOKEN, "")

            if (token.isBlank()) {
                App.globalRequestBeforeCallback
            }

            modifiedRequest = request.newBuilder()
                .addHeader("Authorization", token)
                .build()
        }

        //2.根据请求结果判断用户token是否过期
        val response = chain.proceed(modifiedRequest)
        Log.d(AppConstant.APP_NAME, "intercept: $response")

        val responseBody = response.body?.string()
        if (annotated != null && annotated) {
            val responseData = Gson().fromJson(responseBody, ResponseData::class.java)
            //当请求结果为401,跳转到登陆界面
            if (responseData.code == HttpStatusCode.Unauthorized.code) {
                App.globalRequestAfterCallback(responseData)
            }
        }

        return response.newBuilder()
            .body(ResponseBody.create(response.body?.contentType(), responseBody ?: ""))
            .build()
    }
}