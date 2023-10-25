package com.android.alist.utils

import android.content.Context
import java.nio.channels.spi.AbstractSelectionKey

object SharePreferenceUtils {
    private const val PREF_NAME = "alist-android"

    fun saveString(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(context: Context, key: String, defaultValue: String): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}