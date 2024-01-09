package com.android.alist.ui.compose

sealed class PageConstant(val text: String, val description: String) {
    data object Service : PageConstant("Service", "服务器管理")
    data object File : PageConstant("File", "首页文件管理")
    data object Image : PageConstant("Image", "图片页面")
    data object Video : PageConstant("Video", "视频页面")
}