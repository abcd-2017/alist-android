package com.android.alist

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.android.alist.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var context: Context
        lateinit var db: AppDatabase
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        //app启动,设置全局变量，便于后续自动注入
        context = applicationContext
        db = AppDatabase.getInstance(context)
        instance = this
    }
}