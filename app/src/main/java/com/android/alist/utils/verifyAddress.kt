package com.android.alist.utils

import androidx.core.text.isDigitsOnly

/**
 * 验证ip地址格式是否正确
 */
fun verifyAddress(
    ip: String
): Boolean {
    val splitList = ip.split(".")
    if (splitList.size != 4 || ip.get(0) == '.' || ip.get(ip.length - 1) == '.') return false

    splitList.forEach {
        if (!(it.isDigitsOnly() && it.toInt() in 0..255)) {
            return false
        }
    }
    return true
}