package com.android.alist.utils.constant

/**
 * http响应状态码
 */
enum class HttpStatusCode(val code: Int, val message: String) {
    Success(200, "请求成功"),
    Unauthorized(401, "未授权"),
    NotFound(404, "未找到资源"),
    Error(500, "服务器内部错误")
}