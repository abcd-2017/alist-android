package com.android.alist

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.android.alist.database.AppDatabase
import com.android.alist.network.entity.ResponseData
import com.android.alist.utils.VibratorHelper
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Callback
import retrofit2.Response

@HiltAndroidApp
class App : Application() {
    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var context: Context
        lateinit var db: AppDatabase
        lateinit var instance: App
        lateinit var vibratorHelper: VibratorHelper

        //全局请求之前的回调方法
        lateinit var globalRequestBeforeCallback: () -> Unit

        //全局请求之后的回调方法
        lateinit var globalRequestAfterCallback: (response: ResponseData<*>) -> Unit
    }

    override fun onCreate() {
        super.onCreate()
        //app启动,设置全局变量，便于后续自动注入
        context = applicationContext
        db = AppDatabase.getInstance(context)
        instance = this
        //震动辅助类
        vibratorHelper = VibratorHelper.instance
        vibratorHelper.init(context)
    }
}