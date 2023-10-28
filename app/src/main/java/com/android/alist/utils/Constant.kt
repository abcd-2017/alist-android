package com.android.alist.utils

object Constant {
    const val TOKEN = "token"

    enum class Default(val isDefault: Int) {
        TRUE(1),
        FALSE(0)
    }
}