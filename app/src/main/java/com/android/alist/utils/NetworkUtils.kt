package com.android.alist.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.android.alist.R
import com.android.alist.network.api.FsApi
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.constant.ChannelConstant
import com.android.alist.utils.constant.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import retrofit2.http.Url
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI
import java.net.URL

/**
 * 网络工具
 */
object NetworkUtils {
    /**
     * 下载文件
     */
    suspend fun downloadFile(
        context: Context,
        fileUrl: String,
        fileName: String,
        callback: (message: String) -> Unit
    ) {
        //下载目录
        val downloadDirectory =
            SharePreferenceUtils.getData(
                AppConstant.DEFAULT_DOWNLOAD_PATH,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/alist"
            )
        var outputFile = File(downloadDirectory, fileName)
        outputFile.parent?.let { File(it).mkdir() }
        //如果下载的是文件名是文件夹，则退出下载
        val extension =
            if (outputFile.extension.isNotEmpty()) ".${outputFile.extension}" else {
                outputFile.mkdir()
                return
            }
        var num = 1
        var tempFile = outputFile
        while (tempFile.exists()) {
            val newName = "${outputFile.nameWithoutExtension}($num)$extension"
            tempFile = File(outputFile.parent, newName)
            num++
        }
        outputFile = tempFile

        val notificationManager = NotificationManagerCompat.from(context)
        val notificationId = System.currentTimeMillis().toInt()
        val channelId = ChannelConstant.Download.channelId
        val channelName = ChannelConstant.Download.channelName

        //创建通知渠道
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("文件下载")
            .setContentText("正在下载中...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions 
            return
        }
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())

        val defaultServer = SharePreferenceUtils.getData(AppConstant.DEFAULT_SERVER, "")
        if (defaultServer.isEmpty()) return

        try {
            withContext(Dispatchers.IO) {
                var url = URL("$defaultServer/d$fileUrl/$fileName")
                var urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.instanceFollowRedirects = false

                val responseCode = urlConnection.responseCode
                //如果相应码为重定向，则自动请求重定向的地址
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode ==
                    HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER
                ) {
                    val newPath = urlConnection.getHeaderField("Location")
                    if (newPath.isNotBlank()) {
                        url = URL(newPath)
                        urlConnection = url.openConnection() as HttpURLConnection
                    }
                }
                val input: InputStream = BufferedInputStream(urlConnection.inputStream)
                val output: OutputStream = BufferedOutputStream(FileOutputStream(outputFile))
                val data = ByteArray(8192)
                var total: Long = 0
                var count: Int
                var progress = 0
                val contentLength = urlConnection.contentLength

                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    output.write(data, 0, count)
                    progress = ((total * 100) / contentLength).toInt()

                    // 更新通知栏下载进度
                    notificationBuilder.setProgress(100, progress, false)
                    notificationManager.notify(
                        notificationId,
                        notificationBuilder.build()
                    )
                    if (progress == 100) {
                        notificationManager.cancel(notificationId)
                    }
                }

                output.flush()
                output.close()
                input.close()

                // 创建 PendingIntent
                val intent = Intent(Intent.ACTION_VIEW)
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    outputFile
                )
                intent.setData(contentUri) // 文件 MIME 类型
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                //下载完成，更新通知栏
                notificationBuilder.setContentText("下载完成")
                    .setProgress(0, 0, false)
                    .setContentIntent(pendingIntent) // 设置打开文件的意图
                    .setAutoCancel(true)
                notificationManager.notify(notificationId, notificationBuilder.build())

                callback("下载成功")
            }
        } catch (e: Exception) {
            Log.d(AppConstant.APP_NAME, "downloadFile: $e")
            // 下载出错，更新通知栏
            callback("下载失败，${e.message}")
            notificationBuilder.setContentText("下载失败,${e.message}")
                .setProgress(0, 0, false)
                .setAutoCancel(true)
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    /**
     * 判断当前ip和端口是否可以访问
     */
    fun isConnectable(ip: String, port: Int): Boolean {
        val socket = Socket()
        val address = InetSocketAddress(ip, port)
        try {
            socket.connect(address, AppConstant.REQUEST_TIMEOUT)
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