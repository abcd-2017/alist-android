package com.android.alist.utils

import java.io.IOException
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.TimeUnit

/**
 * 网络工具
 */
object NetworkUtils {
    /**
     * 判断当前ip和端口是否可以访问
     */
    fun isConnectable(ip: String, port: Int): Boolean {
        val socket = Socket()
        val address = InetSocketAddress(ip, port)
        try {
            socket.connect(address, Constant.REQUEST_TIMEOUT)
        } catch (e: IOException) {
            return false
        } finally {
            try {
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }
}