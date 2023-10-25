package com.android.alist.network.annotation

/**
 * 带在api方法上
 * 是否给该方法请求头带上Authorization
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireAuthorization
