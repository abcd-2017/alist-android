package com.android.alist.ui.compose

sealed class PageConstant(val text: String, val description: String) {
    data object Service : PageConstant("Service", "服务器管理")
    data object File : PageConstant("File", "首页文件管理")
}