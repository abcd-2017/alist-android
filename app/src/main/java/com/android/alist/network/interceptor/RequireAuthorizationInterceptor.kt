package com.android.alist.network.interceptor

import android.content.Context
import com.android.alist.network.annotation.RequireAuthorization
import com.android.alist.utils.Constant
import com.android.alist.utils.SharePreferenceUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 验证RequireAuthorization是否存在 拦截器
 * 如果请求方法上带了这个注解，那么就请求头加上token这个参数
 */
class RequireAuthorizationInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val tag = request.tag(retrofit2.Invocation::class.java)
        //调用方法的信息
        val method = tag?.method()
        //是否带有这个标签
        val annotated = method?.annotations?.any { it is RequireAuthorization }

        if (annotated != null && annotated) {
            val token = SharePreferenceUtils.getString(context, Constant.TOKEN, "")

            if (token.isBlank()) {
                // TODO: 当token为空时，应该跳转到登陆界面
            }

            val modifiedRequest = request.newBuilder()
                .addHeader("Authorization", token)
                .build()
            return chain.proceed(modifiedRequest)
        }

        return chain.proceed(request);
    }
}