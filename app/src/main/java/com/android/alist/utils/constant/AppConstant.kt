package com.android.alist.utils.constant

object AppConstant {
    const val APP_NAME = "alist"

    const val TOKEN = "user_token"
    const val DEFAULT_DOWNLOAD_PATH = "default_download_path"
    const val DEFAULT_SERVER = "default_server"
    const val IMAGE_PATH = "image_path"

    const val MAX_PORT = 65535
    const val MIN_PORT = 0
    const val DEFAULT_USERNAME = "admin"
    const val DEFAULT_PASSWORD = "admin"
    const val DEFAULT_PORT = 5244

    enum class Default(val value: Int) {
        TRUE(1),
        FALSE(0)
    }

    enum class ShowContentPage(val currentPage: Int) {
        Page(0),
        Control(1)
    }

    //请求超时时间
    const val REQUEST_TIMEOUT = 3000
}