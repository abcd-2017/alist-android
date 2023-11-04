package com.android.alist.utils

object Constant {
    const val TOKEN = "token"

    enum class Default(val value: Int) {
        TRUE(1),
        FALSE(0)
    }

    //请求超时时间
    const val REQUEST_TIMEOUT = 3000
}